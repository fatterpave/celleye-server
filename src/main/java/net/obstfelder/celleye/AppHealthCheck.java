package net.obstfelder.celleye;

import com.codahale.metrics.health.HealthCheck;

/**
 * Created by obsjoa on 10.02.2017.
 */
public class AppHealthCheck extends HealthCheck
{
    @Override
    protected Result check() throws Exception
    {
        return Result.healthy();
    }
}
