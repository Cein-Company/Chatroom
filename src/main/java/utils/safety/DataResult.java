package utils.safety;

public interface DataResult<T>{
    void result(boolean isSafe,T safeData,ResultConditions conditions);
}
