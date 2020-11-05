package kz.uco.tsadv.modules.recognition.exceptions;

public class EmployeePersonGroupException extends RuntimeException {
    public EmployeePersonGroupException() {
        super("Employee is not linked to the user.");
    }
}
