package analysis.lang.interpreter;

/**
 * Created by: zhang ran
 * 2024-04-06
 */
public class RuntimeError extends RuntimeException{
    RuntimeError(String message){
        super(message);
    }
}
