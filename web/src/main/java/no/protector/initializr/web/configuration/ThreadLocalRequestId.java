package no.protector.initializr.web.configuration;


public class ThreadLocalRequestId {
    private static final ThreadLocal<String> requestId = new ThreadLocal<>();

    private ThreadLocalRequestId() {
        throw new IllegalAccessError("Static Thread Local class");
    }

    public static String get() {
        return requestId.get();
    }

    public static void set(String inputId) {
        requestId.set(inputId);
    }

    public static void unset() {
        requestId.remove();
    }
}
