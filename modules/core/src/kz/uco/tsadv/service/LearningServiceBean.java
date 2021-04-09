package kz.uco.tsadv.service;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.base.importer.exception.ImportFileEofEvaluationException;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonConfig;
import kz.uco.tsadv.importer.utils.XlsHelper;
import kz.uco.tsadv.modules.learning.dictionary.DicTestType;
import kz.uco.tsadv.modules.learning.enums.QuestionType;
import kz.uco.tsadv.modules.learning.enums.TestSectionOrder;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import javax.inject.Inject;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Service(LearningService.NAME)
public class LearningServiceBean implements LearningService {

    @Inject
    protected CommonService commonService;

    @Inject
    private Metadata metadata;

    @Inject
    private FileStorageAPI fileStorageAPI;

    @Inject
    private Persistence persistence;

    @Inject
    private DataManager dataManager;

    @Inject
    protected CommonConfig commonConfig;

    @Inject
    protected FileLoader fileLoader;

    @Override
    public void importQuestionBank(FileDescriptor fileDescriptor) throws Exception {
        Workbook workbook = parseToWorkbook(fileDescriptor);
        int sheetsCount = workbook.getNumberOfSheets();

        if (sheetsCount != 3) {
            throw new RuntimeException("Sheets count must be 3!");
        }
        Map<String, QuestionBank> questionBankMap = parseQuestionBank(workbook.getSheet(workbook.getSheetName(0)));
        Map<String, Question> questionMap = parseQuestion(workbook.getSheet(workbook.getSheetName(1)), questionBankMap);
        parseAnswer(workbook.getSheet(workbook.getSheetName(2)), questionMap);

        ImportLearningLog importLearningLog = metadata.create(ImportLearningLog.class);
        importLearningLog.setFile(fileDescriptor);
        importLearningLog.setLoadingDate(new Date());

        Transaction tx = persistence.createTransaction();
        EntityManager em = persistence.getEntityManager();
        AtomicInteger atomicInteger = new AtomicInteger(0);

        try {
            questionBankMap.values().forEach(questionBank -> {
                em.persist(questionBank);

                List<Question> questions = questionBank.getQuestions();
                questions.forEach(question -> {
                    em.persist(question);

                    List<Answer> answers = question.getAnswers();
                    answers.forEach(answer -> {
                        em.persist(answer);
                        atomicInteger.incrementAndGet();
                    });

                    atomicInteger.incrementAndGet();
                });

                atomicInteger.incrementAndGet();
            });

            tx.commit();

            importLearningLog.setSuccess(true);
        } catch (Exception ex) {
            importLearningLog.setErrorMessage(ex.getMessage());
            importLearningLog.setSuccess(false);
        } finally {
            importLearningLog.setProcessed(atomicInteger.get());

            tx.end();
        }

        dataManager.commit(importLearningLog);
    }

    @Override
    public void importTests(FileDescriptor fileDescriptor) throws Exception {
        Workbook workbook = parseToWorkbook(fileDescriptor);
        int sheetsCount = workbook.getNumberOfSheets();

        if (sheetsCount != 3) {
            throw new RuntimeException("Sheets count must be 3!");
        }

        Transaction tx = persistence.createTransaction();
        EntityManager em = persistence.getEntityManager();
        AtomicInteger atomicInteger = new AtomicInteger(0);

        ImportLearningLog importLearningLog = metadata.create(ImportLearningLog.class);
        importLearningLog.setFile(fileDescriptor);
        importLearningLog.setLoadingDate(new Date());

        try {
            Map<String, Test> testMap = parseTest(getSheet(workbook, 0));
            Map<String, TestSection> testSectionMap = parseTestSection(getSheet(workbook, 1), testMap, em);
            parseQuestionInSection(getSheet(workbook, 2), testSectionMap, em);

            testMap.values().forEach(new Consumer<Test>() {
                @Override
                public void accept(Test test) {
                    em.persist(test);
                    atomicInteger.incrementAndGet();

                    test.getSections().forEach(new Consumer<TestSection>() {
                        @Override
                        public void accept(TestSection testSection) {
                            em.persist(testSection);
                            atomicInteger.incrementAndGet();

                            testSection.getQuestions().forEach(new Consumer<QuestionInSection>() {
                                @Override
                                public void accept(QuestionInSection questionInSection) {
                                    em.persist(questionInSection);
                                    atomicInteger.incrementAndGet();
                                }
                            });
                        }
                    });
                }
            });

            tx.commit();

            importLearningLog.setSuccess(true);
        } catch (Exception ex) {
            importLearningLog.setErrorMessage(ex.getMessage());
            importLearningLog.setSuccess(false);
        } finally {
            importLearningLog.setProcessed(atomicInteger.get());
            tx.end();
        }

        dataManager.commit(importLearningLog);
    }

    @Override
    public String unzipPackage(String packageName, FileDescriptor fileDescriptor) {
        String result = "Error";
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (commonConfig.getScormPackageDirPath() != null && !commonConfig.getScormPackageDirPath().isEmpty()) {
            File file = null;
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            File folder = null;
            try {
                folder = new File(commonConfig.getScormPackageDirPath() + File.separator + packageName);
                if (!folder.exists()) {
                    folder.mkdir();
                } else {
                    throw new RuntimeException("Каталог " + commonConfig.getScormPackageDirPath()
                            + File.separator + packageName + " уже существует");
                }
                inputStream = fileStorageAPI.openStream(fileDescriptor);
                file = new File(folder, fileDescriptor.getName());
                fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
            } catch (FileStorageException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(folder);
            if (isWindows) {
                processBuilder.command("cmd.exe", "/c",
//                        "dir"
                        "jar -xfv \"" + fileDescriptor.getName() + "\""
                );
            } else {
                processBuilder.command("bash", "-c", "unzip \"" + fileDescriptor.getName() + "\"");
            }
            InputStream stdout = null;
            InputStreamReader isrStdout = null;
            BufferedReader brStdout = null;
            try {
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                stdout = process.getInputStream();
                isrStdout = new InputStreamReader(stdout);
                brStdout = new BufferedReader(isrStdout);
                String line = null;
                while ((line = brStdout.readLine()) != null) {
                }
                int exitCode = process.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    isrStdout.close();
                    stdout.close();
                    brStdout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file.delete();
            result = commonConfig.getScormPackageDomainURL() + "/" + packageName + commonConfig.getDefualtIndexHtmlUrl();
        }
        return result;
    }

    @Override
    public boolean deletePackage(String dirName) {
        if (commonConfig.getScormPackageDirPath() != null && !commonConfig.getScormPackageDirPath().isEmpty()) {
            return FileSystemUtils.deleteRecursively(new File(commonConfig.getScormPackageDirPath() + dirName));
        }
        return false;
    }

    /**
     * 0 - question text [string]
     * 1 - section code [string]
     *
     * @param sheet          third sheet from excel Workbook
     * @param testSectionMap map of TestSection, where the code is test section code in sheet
     * @param em             current EntityManager in transaction. Requires an open transaction
     */
    private void parseQuestionInSection(Sheet sheet, Map<String, TestSection> testSectionMap, EntityManager em) throws Exception {
        for (int currentRowIndex = 1; !eof(sheet.getRow(currentRowIndex)); currentRowIndex++) {
            Row row = sheet.getRow(currentRowIndex);

            String questionText = getStringValue(row.getCell(0));
            if (StringUtils.isBlank(questionText)) {
                throw new NullPointerException(String.format("Question name is null in %d row!", currentRowIndex));
            }

            String sectionCode = getStringValue(row.getCell(1));
            if (StringUtils.isBlank(sectionCode)) {
                throw new NullPointerException(String.format("Section code is null in %d row!", currentRowIndex));
            }

            TestSection testSection = testSectionMap.get(sectionCode);
            if (testSection == null) {
                throw new NullPointerException(String.format("TestSection by code %s not found!", sectionCode));
            }

            QuestionInSection questionInSection = metadata.create(QuestionInSection.class);
            questionInSection.setQuestion(findQuestionInQuestionBank(em, testSection.getQuestionBank().getId(), questionText));
            questionInSection.setTestSection(testSection);
            testSection.getQuestions().add(questionInSection);
        }
    }

    /**
     * 0 - code [string]
     * 1 - sectionName [string]
     * 2 - questionOrder [int], values = [FIX(1), RANDOM(2)]
     * 3 - questionBankName [string]
     * 4 - questionPerPage [int]
     * 5 - answerOrder [int], values = [FIX(1), RANDOM(2)]
     * 6 - testCode [string]
     * 7 - generateCount [int]
     * 8 - dynamicLoad [sting], values = [Y, N]
     * 9 - pointsPerQuestion [int]
     *
     * @param sheet   second sheet from excel Workbook
     * @param testMap map of Test, where the key is the test code in sheet
     * @param em      current EntityManager in transaction. Requires an open transaction
     */
    private Map<String, TestSection> parseTestSection(Sheet sheet, Map<String, Test> testMap, EntityManager em) throws Exception {
        Map<String, TestSection> testSectionMap = new HashMap<>();

        for (int currentRowIndex = 1; !eof(sheet.getRow(currentRowIndex)); currentRowIndex++) {
            Row row = sheet.getRow(currentRowIndex);

            String code = getStringValue(row.getCell(0));

            TestSection testSection = metadata.create(TestSection.class);
            testSection.setSectionName(getStringValue(row.getCell(1)));
            testSection.setQuestionOrder(TestSectionOrder.fromId(getIntegerValue(row.getCell(2))));

            String questionBankName = getStringValue(row.getCell(3));
            if (StringUtils.isBlank(questionBankName)) {
                throw new NullPointerException(String.format("QuestionName is null in %d row!", currentRowIndex));
            }

            QuestionBank questionBank = findQuestionBank(questionBankName, em);
            testSection.setQuestionBank(questionBank);
            testSection.setQuestionPerPage(getIntegerValue(row.getCell(4)));
            testSection.setAnswerOrder(TestSectionOrder.fromId(getIntegerValue(row.getCell(5))));
            testSection.setQuestions(new ArrayList<>());

            String testCode = getStringValue(row.getCell(6));
            if (StringUtils.isBlank(testCode)) {
                throw new NullPointerException(String.format("TestCode is null in %d row", currentRowIndex));
            }

            testSection.setGenerateCount(getIntegerValue(row.getCell(7)));
            testSection.setDynamicLoad(getBooleanValue(row.getCell(8)));
            testSection.setPointsPerQuestion(getIntegerValue(row.getCell(9)));

            Test findTest = testMap.get(testCode);
            if (findTest == null) {
                throw new NullPointerException(String.format("Test by code %s not found!", testCode));
            }
            testSection.setTest(findTest);
            findTest.getSections().add(testSection);
            testSectionMap.put(code, testSection);
        }
        return testSectionMap;
    }

    /**
     * 0 - code [string]
     * 1 - name [string]
     * 2 - description [string]
     * 3 - typeCode [string]
     * 4 - active [string], values = [Y, N]
     * 5 - maxAttempt [int]
     * 6 - daysBetweenAttempts [int]
     * 7 - timer [int]
     * 8 - sectionOrder [int], values = [FIX(1), RANDOM(2)]
     * 9 - instruction [string]
     * 10 - targetScore [int]
     * 11 - showResults [string] values [Y, N]
     * 12 - showSectionNewPage [string] values [Y, N]
     * 13 - questionPerPage [int]
     *
     * @param sheet first sheet from excel Workbook
     */
    private Map<String, Test> parseTest(Sheet sheet) throws Exception {
        Map<String, Test> testMap = new HashMap<>();

        for (int currentRowIndex = 1; !eof(sheet.getRow(currentRowIndex)); currentRowIndex++) {
            Row row = sheet.getRow(currentRowIndex);

            String code = getStringValue(row.getCell(0));

            Test test = metadata.create(Test.class);
            test.setName(getStringValue(row.getCell(1)));
            test.setDescription(getStringValue(row.getCell(2)));

            String testTypeCode = getStringValue(row.getCell(3));
            if (StringUtils.isBlank(testTypeCode)) {
                throw new NullPointerException(String.format("The testType column is null on %d row!", currentRowIndex));
            }
            DicTestType dicTestType = commonService.getEntity(DicTestType.class, testTypeCode);
            if (dicTestType == null) {
                throw new NullPointerException(String.format("DicTestType by code : [%s] not found!", testTypeCode));
            }
            test.setType(dicTestType);
            test.setActive(getBooleanValue(row.getCell(4)));
            test.setMaxAttempt(getIntegerValue(row.getCell(5)));
            test.setDaysBetweenAttempts(getIntegerValue(row.getCell(6)));
            test.setTimer(getIntegerValue(row.getCell(7)));
            test.setSectionOrder(TestSectionOrder.fromId(getIntegerValue(row.getCell(8))));
            test.setInstruction(getStringValue(row.getCell(9)));
            test.setTargetScore(getIntegerValue(row.getCell(10)));
            test.setShowResults(getBooleanValue(row.getCell(11)));
            test.setShowSectionNewPage(getBooleanValue(row.getCell(12)));
            test.setQuestionPerPage(getIntegerValue(row.getCell(13)));

            test.setSections(new ArrayList<>());
            testMap.put(code, test);
        }

        return testMap;
    }

    /**
     * Search for a QuestionBank by question bank name.
     *
     * @param questionBankName question bank name
     * @param em               current EntityManager in Transaction. Requires an open transaction
     */
    private QuestionBank findQuestionBank(String questionBankName, EntityManager em) {
        Query query = em.createNativeQuery(
                "SELECT qb.id " +
                        "FROM tsadv_question_bank qb " +
                        "WHERE qb.delete_ts IS NULL " +
                        "      AND qb.bank_name = ?1");
        query.setParameter(1, questionBankName);
        Object resultObject = query.getSingleResult();
        if (resultObject != null) {
            return em.getReference(QuestionBank.class, (UUID) resultObject);
        }

        throw new NullPointerException(String.format("Question by name %s not found!", questionBankName));
    }

    /**
     * 0 - text [string]
     * 1 - correct [string]
     * 2 - question code [string]
     *
     * @param sheet       third sheet from excel Workbook
     * @param questionMap map of Question, where the key is the question code in sheet
     */
    private void parseAnswer(Sheet sheet, Map<String, Question> questionMap) throws Exception {
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            String text = getStringValue(row.getCell(0));
            if (StringUtils.isBlank(text)) break;

            Answer answer = metadata.create(Answer.class);
            answer.setAnswer(text);

            String correct = getStringValue(row.getCell(1));
            if (correct.equalsIgnoreCase("y")) {
                correct = "true";
            }
            answer.setCorrect(Boolean.parseBoolean(correct));

            String questionCode = getStringValue(row.getCell(2));
            Question findQuestion = questionMap.get(questionCode);
            if (findQuestion == null) {
                throw new NullPointerException("Question is not found!");
            }

            answer.setQuestion(findQuestion);

            findQuestion.getAnswers().add(answer);
        }
    }

    /**
     * 0 - code [string]
     * 1 - text [string]
     * 2 - type [string], values = [TEXT, ONE, MANY, NUM]
     * 3 - score [int]
     * 4 - bankCode [string]
     *
     * @param sheet           second sheet from excel Workbook
     * @param questionBankMap map of QuestionBank, where the key is the question bank code in sheet
     */
    private Map<String, Question> parseQuestion(Sheet sheet, Map<String, QuestionBank> questionBankMap) throws Exception {
        Map<String, Question> questionMap = new HashMap<>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);

            String code = getStringValue(row.getCell(0));

            if (StringUtils.isBlank(code)) break;

            Question question = metadata.create(Question.class);
            question.setText(getStringValue(row.getCell(1)));
            question.setType(Enum.valueOf(QuestionType.class, getStringValue(row.getCell(2)).toUpperCase()));
            question.setScore(getIntegerValue(row.getCell(3)));
            question.setAnswers(new ArrayList<>());

            String bankCode = getStringValue(row.getCell(4));
            QuestionBank finaQuestionBank = questionBankMap.get(bankCode);
            if (finaQuestionBank == null) {
                throw new NullPointerException("Error while parsing question [Question is not found!]");
            }
            question.setBank(finaQuestionBank);

            finaQuestionBank.getQuestions().add(question);

            questionMap.put(code, question);
        }

        return questionMap;
    }

    /**
     * 0 - code [string]
     * 1 - name [string]
     * 2 - description [string]
     *
     * @param sheet first sheet from excel Workbook
     */
    private Map<String, QuestionBank> parseQuestionBank(Sheet sheet) throws Exception {
        Map<String, QuestionBank> questionBankMap = new HashMap<>();

        for (int currentRowIndex = 1; !eof(sheet.getRow(currentRowIndex)); currentRowIndex++) {
            Row row = sheet.getRow(currentRowIndex);

            String code = getStringValue(row.getCell(0));

            QuestionBank questionBank = metadata.create(QuestionBank.class);
            questionBank.setBankName(getStringValue(row.getCell(1)));
            questionBank.setDescription(getStringValue(row.getCell(2)));
            questionBank.setQuestions(new ArrayList<>());
            questionBankMap.put(code, questionBank);
        }

        return questionBankMap;
    }

    /**
     * Requires an open transaction
     *
     * @param em             current EntityManager in transaction. Requires an open transaction
     * @param questionBankId question bank ID where you need to search
     * @param questionText   search question text
     */
    private Question findQuestionInQuestionBank(EntityManager em, UUID questionBankId, String questionText) {
        Query query = em.createNativeQuery(
                "SELECT q.id " +
                        "FROM tsadv_question q " +
                        "WHERE q.delete_ts is null " +
                        "   AND q.bank_id = ?1 " +
                        "   AND q.text = ?2");
        query.setParameter(1, questionBankId);
        query.setParameter(2, questionText);

        Object result = query.getSingleResult();
        if (result != null) {
            return em.getReference(Question.class, (UUID) result);
        }

        throw new NullPointerException(String.format("Question by name [%s] not in QuestionBank [%s]", questionText, questionBankId));
    }

    private String getStringValue(Cell cell) {
        if (cell != null && cell.getCellTypeEnum() != null && cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
            return Double.toString(cell.getNumericCellValue());
        }
        return cell != null ? cell.getStringCellValue() : null;
    }

    private Boolean getBooleanValue(Cell cell) {
        if (cell == null) return false;
        String value = cell.getStringCellValue();
        if (StringUtils.isBlank(value)) {
            value = "false";
        } else {
            if (value.equalsIgnoreCase("y")) {
                value = "true";
            }
        }
        return Boolean.parseBoolean(value);
    }

    private Integer getIntegerValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        Double value = cell.getNumericCellValue();
        return value.intValue();
    }

    private boolean eof(Row row) throws Exception {
        return eofByColumnNullValue(row, 0);
    }

    private Sheet getSheet(Workbook workbook, int sheetIndex) {
        return workbook.getSheet(workbook.getSheetName(sheetIndex));
    }

    /**
     * @param columnNumber the index of the cell being checked
     * @param row          row from sheet
     * @return True if the first cell in row is null or empty, this means the end of the file.
     */
    protected Boolean eofByColumnNullValue(Row row, int columnNumber) throws ImportFileEofEvaluationException {
        if (row != null) {
            Cell cell = row.getCell(columnNumber);
            Object cellValue;
            try {
                cellValue = XlsHelper.getCellValue(cell);
            } catch (Exception e) {
                throw new ImportFileEofEvaluationException(String.format("Eof evaluation has failed on row %s", row.getRowNum()));
            }
            return cellValue == null;
        }
        return true;
    }

    private Workbook parseToWorkbook(FileDescriptor fileDescriptor) throws Exception {
        byte[] xlsFile = fileStorageAPI.loadFile(fileDescriptor);
        Workbook workbook = XlsHelper.openWorkbook(xlsFile);

        if (workbook == null)
            throw new FileStorageException(FileStorageException.Type.FILE_NOT_FOUND, "File was not loaded");

        return workbook;
    }

    @Override
    public boolean allCourseSectionPassed(List<CourseSection> courseSectionList) {
        boolean success = true;
        for (CourseSection section : courseSectionList) {
            List<CourseSectionAttempt> courseSectionAttemptList = dataManager.load(CourseSectionAttempt.class)
                    .query("select e from tsadv$CourseSectionAttempt e " +
                            " where e.success = true " +
                            " and e.courseSection = :section")
                    .parameter("section", section)
                    .view("courseSectionAttempt.edit")
                    .list();
            if (courseSectionAttemptList.size() < 1) {
                success = false;
            }
        }
        return success;
    }

    @Override
    public boolean allHomeworkPassed(List<Homework> homeworkList, PersonGroupExt personGroup) {
        boolean success = true;
        for (Homework homework : homeworkList) {
            List<StudentHomework> studentHomeworks = dataManager.load(StudentHomework.class)
                    .query("select e from tsadv_StudentHomework e " +
                            " where e.isDone = true " +
                            " and e.homework = :homework " +
                            " and e.personGroup = :personGroup")
                    .parameter("homework", homework)
                    .parameter("personGroup", personGroup)
                    .view("studentHomework.edit")
                    .list();
            if (studentHomeworks.size() < 1) {
                success = false;
            }
        }
        return success;
    }
}