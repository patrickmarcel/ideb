package errors;

public enum MatrixError {
    FIRST_NULL("First matrix parameter is null"),
    LATTER_NULL("Latter matrix parameter is null"),
    FIRST_NOT_RECTANGULAR("First matrix parameter isn't well formed. It's either empty or rows aren't the same size"),
    LATTER_NOT_RECTANGULAR("Latter matrix parameter isn't well formed. It's either empty or rows aren't the same size"),
    MISSHAPEN("The matrices are misshapen and cannot be multiplied"),
    ;

    private String message;

    MatrixError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
