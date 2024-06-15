package  services;
public class ConcreteLoggingService implements LoggingService {
    @Override
    public void log(String message) {
        System.out.println("Logging: " + message);
    }
}