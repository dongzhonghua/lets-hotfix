package com.github.lzy.hotfix.registry;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import lombok.extern.slf4j.Slf4j;

/**
 * LocalModeRegistryService Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>11月 1, 2020</pre>
 */
@Slf4j
public class LocalModeRegistryServiceTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: findAllInstances()
     */
    @Test
    public void testFindAllInstances() throws Exception {
        //TODO: Test goes here...
        LocalModeRegistryService localModeRegistryService = new LocalModeRegistryService();
        log.info(String.valueOf(localModeRegistryService.findAllInstances()));
    }


} 
