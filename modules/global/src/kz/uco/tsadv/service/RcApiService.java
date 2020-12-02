package kz.uco.tsadv.service;


import kz.uco.tsadv.api.*;

import java.util.UUID;

/**
 * <h1><b>API для Модуля Рекрутинг</b></h1>
 * <br>
 * Сущность это объект (структура с данными) <br><p>
 * В методах, там где встречается Id сущности используется UUID идентификаторы <br><p>
 * Там где в параметрах встречается код языка он используется для извлечения названий сущностей на заданном языке <br><p>
 * Везде где используется какая-либо сущность, зачастую там присутствует не внутренняя сущность системы,
 * а "обрезанная" интеграционнная сущность только с необходимыми внешним системам полями <br><p>
 * Вакансия - это как заявка на новую вакансию руководителем подразделения компании, а также сама вакансия после утверждения заявки <br><p>
 * Отклик - это отклик кандидатом на какую-либо вакансию
 * <br><p>
 * URL для вызова сервисов <b>http://kchrapp.air-astana.net/aa/rest/v2/services/tsadv_RcApiService/{имяМетода}</b> <br>
 * Где имяМетода меняется в зависимости от того какой именно метод сервиса вы вызываете, например: getCountries или getRequisitions <br>
 * <p>
 * <b>Запрос для Авторизация:</b><br>
 * URL сервиса <b>http://kchrapp.air-astana.net/aa/rest/v2/oauth/token</b><br>
 * Метод: POST <br>
 * Параметры: В строке запроса (QUERY): <br>
 * - grant_type (string) Тип авторизации, константа 'password' <br>
 * - username (string) Логин <br>
 * - password (string) Пароль <br>
 * Параметры: В заголовке (header): <br>
 * - Authorization = Basic Base64('client:secret') <br>
 * - Content-Type = 'application/x-www-form-urlencoded' <br>
 * <p>
 * Как в целом работать с REST сервисами описано <a href="https://files.cuba-platform.com/swagger/6.8/">https://files.cuba-platform.com/swagger/6.8/</a> <br>
 * в частности: <br>
 * Аутентификация <a href="https://files.cuba-platform.com/swagger/6.8/#/OAuth">https://files.cuba-platform.com/swagger/6.8/#/OAuth</a> <br>
 * Вызов сервисов <a href="https://files.cuba-platform.com/swagger/6.8/#/Services">https://files.cuba-platform.com/swagger/6.8/#/Services</a><br><p>
 * Ниже в данном документе вы найдёте список сервисов с описанием названия конкретного сервиса,
 * названиями и типами принимаемых параметров, а также методов HTTP запроса и необходимостью авторизации  <br><p>
 * <b>Статсусы HTTP могут быть:</b> <br>
 * - 200 (OK) <br>
 * - 400 (Bad Request) Неверно указано название сервиса <br>
 * - 401 (Unauthorized) Не авторизовано <br>
 * - 500 (Internal Server Error) В случае ошибки в сервисе (требует исправления разработчиками) <br>
 */
public interface RcApiService {
    String NAME = "tsadv_RcApiService";

    /* get methods */

    /**
     * Возвращает описание Структуры данных интеграционных сущностей в формате JSON
     * <br><p><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     */
    String getAllObjectModel();

    /**
     * Возвращает список стран
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Страна в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getCountries(String lang);

    /**
     * Возвращает список городов для заданных стран
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param country Id стран разделённых запятыми
     * @param lang    Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Город в формате JSON  <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getCities(String country, String lang);

    /**
     * Возвращает список Категорий Вакансий
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Категория должности в формате JSON  <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getRequisitionCategories(String lang);

    /**
     * Возвращает Вакансии соответствующие заданным параметрам
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param code      Код заявки (а точнее часть кода)
     * @param job       Часть штатной единицы
     * @param country   Список Id стран через запятую
     * @param city      Список Id городов через запятую
     * @param category  Список Id Категорий должности через запятую
     * @param startDate Дата начала Вакансии в формате гггг-мм-дд
     * @param lang      Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Вакансия в формате JSON <br>
     * - code (string) Код вакансии <br>
     * - requiredTest (boolean) Требуется ли Прескрининг тест <br>
     * - videoInterviewRequired(boolean)Требуется ли интервью по Skype <br>
     * - job (string) Должность <br>
     * - posCount (integer)  <br>
     * - cityName (string) Название города <br>
     * - city (uuid) Id города <br>
     * - categoryName (string) <br>
     * - category (uuid) <br>
     * - startDate (string) Дата начала вакансии <br>
     * - endDate (string) Дата окончания вакансии <br>
     * - finalDate (string <br>
     * - description (string) Описание вакансии <br>
     * - country (uuid) Id страны <br>
     * - countryName (string) Наименование страны <br>
     * - latitude (double) Широта <br>
     * - longitude (double) Долгота <br>
     */
    String getRequisitions(
            String code,
            String job,
            String country,
            String city,
            String category,
            String startDate,
            String lang
    );

    String getRequisitionsByPerson(
            String code,
            String job,
            String country,
            String city,
            String category,
            String startDate,
            String lang
    );

    /**
     * Возвращает Вакансию
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param requisition Id вакансии
     * @param lang        Код языка RU/KZ/EN для извлечения названий
     * @return Возвращает сущность Вакансия в формате JSON <br>
     * - code (string) Код вакансии <br>
     * - requiredTest (boolean) Требуется ли Прескрининг тест <br>
     * - videoInterviewRequired(boolean)Требуется ли интервью по Skype <br>
     * - job (string) Должность <br>
     * - posCount (integer)  <br>
     * - cityName (string) Название города <br>
     * - city (uuid) Id города <br>
     * - categoryName (string) <br>
     * - category (uuid) <br>
     * - startDate (string) Дата начала вакансии <br>
     * - endDate (string) Дата окончания вакансии <br>
     * - finalDate (string <br>
     * - description (string) Описание вакансии <br>
     * - country (uuid) Id страны <br>
     * - countryName (string) Наименование страны <br>
     * - latitude (double) Широта <br>
     * - longitude (double) Долгота <br>
     */
    String getRequisition(UUID requisition, String lang);

    /**
     * Возвращает Имеет ли заданная вакансия отклики текущим пользователем
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param requisition Id Вакансии
     * @return Сущность Результат в формате JSON <br>
     * - result (string) Результат true - Да / false - Нет <br>
     * - errorMessage (string) Текст ошибки <br>
     */
    String hasJobRequestByUser(UUID requisition);

    /**
     * Проверяет профиль кандидата на полноту для заданного Id пользователя
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param user Id пользователя системы
     * @return Сущность Результат в формате JSON <br>
     * - result (string) Результат true - профиль кандидата полный / false - профиль кандидата не полный <br>
     * - errorMessage (string) Текст ошибки, один из: <br>
     * &nbsp;&nbsp; PersonExperience is empty! - Отсутствует информация об опыте работы <br>
     * &nbsp;&nbsp; PersonEducation is empty! - Отсутствует информация об образовании <br>
     * &nbsp;&nbsp; PersonContact is empty! - Отсутствует информация о контактных лицах кандидата <br>
     * &nbsp;&nbsp; PersonGroup is empty! - Кандитат не определён (не найден) <br>
     */
    String checkFullProfile(UUID user);

    /**
     * Возвращает Прескрининг Опросник (Тест) для заданной вакансии
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param requisition Id вакансии
     * @param lang        Код языка RU/KZ/EN для извлечения названий
     * @return Сущность Опросник в формате JSON <br>
     * - name (string) Название опросника
     * - questions [question] Список вопросов
     */
    String getPreScreenTest(UUID requisition, String lang);

    /**
     * Возвращает Справочник Пол
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Пол в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getSex(String lang);

    /**
     * Возвращает справочник Национальность
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список Сущностей Национальность в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getNationalities(String lang);

    /**
     * Возвращает справочник Страна Гражданства
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список Сущностей Гражданство в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getCitizenship(String lang);

    /**
     * Возвращает справочник Семейное положение
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список Сущностей Семейное положение в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getMaritalStatuses(String lang);

    /**
     * Возвращает информацию о текущем Кандидате
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Сущность Лицо в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getUserPerson(String lang);

    /**
     * Возвращает справочник Спепень образования
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Спепень образования в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getEducationDegrees(String lang);

    /**
     * Возвращает справочник Уровень образования
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Уровень образования в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getEducationLevels(String lang);

    /**
     * Возвращает справочник Тип телефона
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Тип телефона в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getContactTypes(String lang);


    String getAttachmentCategories(String lang);

    String getCompetences(String lang);

    String getScaleLevels(UUID competence, String lang);

    String getScheduledInterviews(UUID requisition, String lang);

    String getUserInterviews(String lang);

    String getUserJobRequests(String lang);

    /* post methods */

    /**
     * Создаёт в системе кандидата, пользователя, с информацией об образовании, опыте, навыках, и т.д. ... <br>
     * <br><p>
     * Метод: POST <br>
     * Авторизация: Не требуется  <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param user Информация о кандидате в формате JSON <br>
     *             - login (string) Логин <br>
     *             - language (string) Язык интерфейса <br>
     *             - password (string) Пароль <br>
     *             - email (string) Email адрес <br>
     *             - phoneNumber (string) Номер телефона <br>
     *             - person (<i><b>Person</b></i>) Персональные данные <br>
     *             <p>
     *             <b>Person</b> Лицо: <br>
     *             - firstName (string) Имя <br>
     *             - lastName (string) Фамилия <br>
     *             - middleName (string) Отчество <br>
     *             - birthDate (string) Дата рождения <br>
     *             - nationalIdentifier (string) ИИН <br>
     *             - sex (uuid) Id пола <br>
     *             - sexName (string) Пол <br>
     *             - nationality (uuid) Id национальности <br>
     *             - nationalityName (string) Национальность <br>
     *             - citizenship (uuid) Id гражданства <br>
     *             - citizenshipName (string) Гражданство <br>
     *             - maritalStatus (uuid) Id семейного положения <br>
     *             - maritalStatusName (string) Семейное положение <br>
     *             - education (list<<i><b>PersonEducation</b></i>>) Образование <br>
     *             - contacts (list<<i><b>PersonContact</b></i>>) Контактная информация<br>
     *             - experience (list<<i><b>PersonExperience</b></i>>) Опыт работы <br>
     *             - attachments (list<<i><b>PersonAttachment</b></i>>) Приложения <br>
     *             - competences (list<<i><b>PersonCompetence</b></i>>) Навыки <br>
     *             - photo (string) Фото <br>
     *             - cityName (string) Город <br>
     *             <p>
     *             <b>PersonEducation</b> Образование: <br>
     *             - school (string) Наименование Учебного Заведения <br>
     *             - startYear (integer) Год начала обучения <br>
     *             - endYear (integer) Год окончания обучения <br>
     *             - specialization (string) Специальность <br>
     *             - level (uuid) Id уровня образования <br>
     *             - levelName (string) Уровень образования <br>
     *             - degree (uuid) Id Степени <br>
     *             - degreeName (string) Степень <br>
     *             - location (string) Адрес учебного заведения <br>
     *             - bolashak (boolean) Участник программы Болашак (true / false) <br>
     *             <p>
     *             <b>PersonContact</b> Контакт: <br>
     *             - contactType (UUID) Id типа контактной информации <br>
     *             - contactTypeName (String) Тип контактной информации <br>
     *             - contactValue (String) Адрес/Номер <br>
     *             <p>
     *             <b>PersonExperience</b> Опыт работы: <br>
     *             - company (String) Компания <br>
     *             - untilNow (Boolean) По настоящее время (true / false) <br>
     *             - job (String) Должность <br>
     *             - startMonth (String) Год, Месяц начала работы <br>
     *             - endMonth (String) Год, Месяц окончания работы <br>
     *             - description (String) Примечание <br>
     *             <p>
     *             <b>PersonAttachment</b> Приложение: <br>
     *             - category (UUID) Id категории <br>
     *             - categoryName (String) Категория <br>
     *             - filename (String) Имя файла <br>
     *             - file (UUID) Id файла <br>
     *             - description (String) Примечание <br>
     *             <p>
     *             <b>PersonCompetence</b> Навык: <br>
     *             - competence (UUID) Id Навыка <br>
     *             - competenceName (String) Навык <br>
     *             - scaleLevel (UUID) Id уровня владения навыком <br>
     *             - scaleLevelName (String) Уровень владения навыком <br>
     *             - competenceTypeCode (String) Код типа навыка <br>
     * @return Результат операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    BaseResult registerUser(UserInt user);

    BaseResult addInterviewAnswer(InterviewQuestionnaireInt interviewQuestionnaireInt);

    BaseResult uploadUserPhoto(PersonInt person);

    BaseResult requestInterview(UUID interview);

    BaseResult updatePerson(PersonInt person);

    /**
     * Создаёт контактную информацию <br>
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param contact Контактная информация: <br>
     *                - contactType (UUID) Id типа контактной информации <br>
     *                - contactTypeName (String) Тип контактной информации <br>
     *                - contactValue (String) Адрес/Номер <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    BaseResult createContact(PersonContactInt contact);

    BaseResult updateContact(PersonContactInt contact, UUID id);

    BaseResult deleteContact(UUID id);

    /**
     * Создаёт Сведения об образовании <br>
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param education Образование: <br>
     *                  - school (string) Наименование Учебного Заведения <br>
     *                  - startYear (integer) Год начала обучения <br>
     *                  - endYear (integer) Год окончания обучения <br>
     *                  - specialization (string) Специальность <br>
     *                  - level (uuid) Id уровня образования <br>
     *                  - levelName (string) Уровень образования <br>
     *                  - degree (uuid) Id Степени <br>
     *                  - degreeName (string) Степень <br>
     *                  - location (string) Адрес учебного заведения <br>
     *                  - bolashak (boolean) Участник программы Болашак (true / false) <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    BaseResult createEducation(PersonEducationInt education);

    BaseResult updateEducation(PersonEducationInt education, UUID id);

    BaseResult deleteEducation(UUID id);

    /**
     * Создаёт Сведения об Опыте работы <br>
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param experience Опыт работы: <br>
     *                   - company (String) Компания <br>
     *                   - untilNow (Boolean) По настоящее время (true / false) <br>
     *                   - job (String) Должность <br>
     *                   - startMonth (String) Год, Месяц начала работы <br>
     *                   - endMonth (String) Год, Месяц окончания работы <br>
     *                   - description (String) Примечание <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    BaseResult createExperience(PersonExperienceInt experience);

    BaseResult updateExperience(PersonExperienceInt experience, UUID id);

    BaseResult deleteExperience(UUID id);

    /**
     * Создаёт Приложение (Вложение)
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param attachment Приложение (Вложение): <br>
     *                   - category (UUID) Id категории <br>
     *                   - categoryName (String) Категория <br>
     *                   - filename (String) Имя файла <br>
     *                   - file (UUID) Id файла <br>
     *                   - description (String) Примечание <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    BaseResult createAttachment(PersonAttachmentInt attachment);

    BaseResult updateAttachment(PersonAttachmentInt attachment, UUID id);

    BaseResult deleteAttachment(UUID id);

    BaseResult createCompetence(PersonCompetenceInt competence);

    BaseResult updateCompetence(PersonCompetenceInt competence, UUID id);

    BaseResult deleteCompetence(UUID id);

    /**
     * Возвращает справочник Источник
     * <br><p>
     * Метод: GET <br>
     * Авторизация: Не требуется <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param lang Код языка RU/KZ/EN для извлечения названий
     * @return Список сущностей Источник в формате JSON <br>
     * - code (string) Код <br>
     * - name (string) Наименование <br>
     * - id   (uuid) Id сущности <br>
     */
    String getSources(String lang);

    String getInterviewStatusEnum(String lang);

    String getOfferStatusEnum(String lang);

    String getJobRequestStatusEnum(String lang);

    /**
     * Откликается на Вакансию (Создаёт в системе отклик) <br>
     * <br><p>
     * Метод: POST <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В теле запроса в формате JSON (BODY) <br>
     *
     * @param jobRequestInt Отклик в формате JSON <br>
     *                      - requestDate (String) Дата отклика <br>
     *                      - requestStatus (String) Статус отклика <br>
     *                      - requisition (UUID) Id Вакансии <br>
     *                      - requisitionCode (String) Код Вакансии <br>
     *                      - requisitionJob (String) Должность <br>
     *                      - videoFile (UUID) Id видеозаписи о себе <br>
     *                      - source (UUID) Id источника отклика <br>
     *                      - otherSource (String) Прочий источник (отсутствующий в справочнике источников) <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    BaseResult requestRequisition(JobRequestInt jobRequestInt);

    BaseResult approveInterview(UUID interview);

    BaseResult cancelInterview(UUID interview, String reason);

    BaseResult updateUser(UserInt user);

    String getUser(String lang);

    BaseResult changePassword(String oldPassword, String newPassword);

    String getOffers(String lang);

//    BaseResult approveUserOffer(UUID offer);

//    BaseResult rejectUserOffer(OfferInt offerInt);

    BaseResult saveVideoFile(UUID requisition, UUID videoFile);

    /**
     * Добавляет Для текущего пользователя Адрес созданный по заданному городу <br>
     * <br><p>
     * Метод: GET <br>
     * Авторизация: OAuth-токен должен быть передан в заголовке запроса Authorization с типом Bearer. <br>
     * Передача параметров: В строке запроса (QUERY) <br>
     *
     * @param addressInt Id Города <br>
     * @return Результат выполнения операции в формате JSON <br>
     * - success (boolean) Успешно ли (true / false) <br>
     * - successMessage (String) Сообщение при успехе <br>
     * - errorMessage (String) Сообщение при ошибке <br>
     */
    BaseResult addAddress(UUID addressInt);

    String getAddress(String lang);

    String getAddressType(String lang);

    BaseResult deleteAddress(UUID id);

    BaseResult updateAddress(PersonAddressInt addressInt);

    BaseResult checkVideoInterview(CandidateInt candidateInt);

    BaseResult checkPrescreeningTest(CandidateInt candidateInt);

    BaseResult createPersonDocument(PersonDocumentInt personDocumentInt);

    BaseResult createAgreement(AgreementInt agreementInt);

    BaseResult cancelJobRequest(UUID jobRequestId);

    Boolean isEmployeeWithPersonGroupId();
}