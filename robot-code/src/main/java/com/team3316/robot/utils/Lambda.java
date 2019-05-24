package com.team3316.robot.utils;

/**
 * A 1-parameter lambda function
 */
public class Lambda {
  public interface Lambda1<T> {
    void compute (T t);
  }

  public interface Lambda2<T, U> {
    void compute (T t, U u);
  }

  public interface Lambda3<T, U, S> {
    void compute (T t, U u, S s);
  }
}
