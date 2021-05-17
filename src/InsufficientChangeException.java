

public class InsufficientChangeException extends RuntimeException {
    String msg = "";
    
    public InsufficientChangeException(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return this.msg;
    }
}
