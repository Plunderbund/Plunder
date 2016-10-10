package com.plunder.plunder.executors;

import android.support.annotation.NonNull;
import java.util.concurrent.Future;

public interface RunnableExecutor {
  Future<?> run(@NonNull Runnable runnable);
}
