public class SoldOutException extends RuntimeException {
    private String error = "";

    public SoldOutException(String error) {
        this.error = error;
    }

    public String getMessage() {
        return this.error;
    }
}
