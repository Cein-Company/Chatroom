package utils.safety;

public class Security {

    public static <T> void checkData(T data, DataResult result) {
        /* DO SOMETHING */
        result.result(true, data, ResultConditions.Safe);
    }

}
