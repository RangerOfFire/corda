package net.corda.core.concurrent;

import net.corda.core.internal.concurrent.OpenFuture;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static net.corda.core.internal.concurrent.CordaFutureImplKt.doneFuture;
import static net.corda.core.internal.concurrent.CordaFutureImplKt.openFuture;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CordaFutureInJavaTest {
    @Test
    public void methodsAreNotTooAwkwardToUse() throws InterruptedException, ExecutionException {
        {
            CordaFuture<Number> f = openFuture();
            f.cancel(false);
            assertTrue(f.isCancelled());
        }
        {
            CordaFuture<Number> f = openFuture();
            assertThatThrownBy(() -> f.get(1, TimeUnit.MILLISECONDS)).isInstanceOf(TimeoutException.class);
        }
        {
            CordaFuture<Number> f = doneFuture(100);
            assertEquals(100, f.get());
        }
        {
            Future<Integer> f = doneFuture(100);
            assertEquals(Integer.valueOf(100), f.get());
        }
        {
            OpenFuture<Number> f = openFuture();
            OpenFuture<Number> g = openFuture();
            f.then(done -> {
                try {
                    return g.set(done.get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
            f.set(100);
            assertEquals(100, g.get());
        }
    }
}
