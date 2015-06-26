/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.util.translation;/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.easymock.EasyMock.expect;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@PrepareForTest({TranslateSentenceTask.class, TranslateTask.class})
@PowerMockIgnore({"javax.management.*", "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*",
        "ch.qos.logback.*", "org.slf4j.*"})
public class TranslateTaskTest extends EasyMockSupport {
    @Mock
    private ExecutorService service;
    @Mock
    private TranslationProvider provider;
    @Mock
    private TranslatorCallback callback;

    /**
     * Test if the translation task correctly splits a complex chat line into sentences.
     *
     * @throws Exception
     */
    @SuppressWarnings({"ConstantConditions", "unchecked", "OverlyLongMethod"})
    @Test
    public void testSentenceSplit() throws Exception {
        service = createMock(ExecutorService.class);
        provider = createMock(TranslationProvider.class);
        callback = createMock(TranslatorCallback.class);
        replayAll();

        Callable task = new TranslateTask(service, provider, TranslationDirection.EnglishToGerman,
                "Hello. This is a multi sentence line! It should be split. Even if the last part is not terminated",
                callback);
        verifyAll();
        resetAll();

        // until now nothing should happen to ensure that the creation of the task is very cheap
        // the next thing is calling the task and that should actually do things.
        Future<String> firstFuture = createMock(Future.class);
        expect(firstFuture.get()).andReturn("Hallo.").anyTimes();
        TranslateSentenceTask task1Mock = PowerMock.createMock(TranslateSentenceTask.class);
        PowerMock.expectStrictNew(TranslateSentenceTask.class,
                new Class<?>[]{TranslationProvider.class, TranslationDirection.class, String.class},
                provider, TranslationDirection.EnglishToGerman, "Hello.")
                .andReturn(task1Mock);
        expect(service.submit(task1Mock)).andReturn(firstFuture);

        Future<String> secondFuture = createMock(Future.class);
        expect(secondFuture.get()).andReturn("Das ist eine Zeile mit mehreren Sätzen.").anyTimes();
        TranslateSentenceTask task2Mock = PowerMock.createMock(TranslateSentenceTask.class);
        PowerMock.expectStrictNew(TranslateSentenceTask.class,
                new Class<?>[]{TranslationProvider.class, TranslationDirection.class, String.class},
                provider, TranslationDirection.EnglishToGerman,
                "This is a multi sentence line!").andReturn(task2Mock);
        expect(service.submit(task2Mock)).andReturn(secondFuture);

        Future<String> thirdFuture = createMock(Future.class);
        expect(thirdFuture.get()).andReturn("Sie sollte geteilt werden.").anyTimes();
        TranslateSentenceTask task3Mock = PowerMock.createMock(TranslateSentenceTask.class);
        PowerMock.expectStrictNew(TranslateSentenceTask.class,
                new Class<?>[]{TranslationProvider.class, TranslationDirection.class, String.class},
                provider, TranslationDirection.EnglishToGerman,
                "It should be split.").andReturn(task3Mock);
        expect(service.submit(task3Mock)).andReturn(thirdFuture);

        Future<String> fourthFuture = createMock(Future.class);
        expect(fourthFuture.get()).andReturn("Auch wenn der letzte Teil nicht abgeschlossen ist").anyTimes();
        TranslateSentenceTask task4Mock = PowerMock.createMock(TranslateSentenceTask.class);
        PowerMock.expectStrictNew(TranslateSentenceTask.class,
                new Class<?>[]{TranslationProvider.class, TranslationDirection.class, String.class},
                provider, TranslationDirection.EnglishToGerman,
                "Even if the last part is not terminated").andReturn(task4Mock);
        expect(service.submit(task4Mock)).andReturn(fourthFuture);

        callback.sendTranslation("Hallo. Das ist eine Zeile mit mehreren Sätzen. Sie sollte geteilt werden. Auch wenn" +
                " der letzte Teil nicht abgeschlossen ist");

        PowerMock.replay(task1Mock, task2Mock, task3Mock, task4Mock);
        PowerMock.replay(TranslateSentenceTask.class);
        replayAll();
        // Everything we expect is recorded. Let's do this.

        task.call();

        PowerMock.verify(task1Mock, task2Mock, task3Mock, task4Mock);
        PowerMock.verify(TranslateSentenceTask.class);
        verifyAll();
    }

    /**
     * Test if the translation task correctly excludes the default headers from the text.
     *
     * @throws Exception
     */
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test
    public void testHeaderExclusion1() throws Exception {
        service = createMock(ExecutorService.class);
        provider = createMock(TranslationProvider.class);
        callback = createMock(TranslatorCallback.class);
        replayAll();

        Callable task = new TranslateTask(service, provider, TranslationDirection.EnglishToGerman,
                "You hear: ALL YOUR BASE ARE BELONG TO US.",
                callback);
        verifyAll();
        resetAll();

        // until now nothing should happen to ensure that the creation of the task is very cheap
        // the next thing is calling the task and that should actually do things.
        Future<String> translationFuture = createMock(Future.class);
        expect(translationFuture.get()).andReturn("ALL DEINE STÜTZPUNKT SIND GEHÖREN UNS.").anyTimes();
        TranslateSentenceTask sentenceTask = PowerMock.createMock(TranslateSentenceTask.class);
        PowerMock.expectStrictNew(TranslateSentenceTask.class,
                new Class<?>[]{TranslationProvider.class, TranslationDirection.class, String.class},
                provider, TranslationDirection.EnglishToGerman, "ALL YOUR BASE ARE BELONG TO US.")
                .andReturn(sentenceTask);
        expect(service.submit(sentenceTask)).andReturn(translationFuture);

        callback.sendTranslation("You hear: ALL DEINE STÜTZPUNKT SIND GEHÖREN UNS.");

        PowerMock.replay(sentenceTask);
        PowerMock.replay(TranslateSentenceTask.class);
        replayAll();
        // Everything we expect is recorded. Let's do this.

        task.call();

        PowerMock.verify(sentenceTask);
        PowerMock.verify(TranslateSentenceTask.class);
        verifyAll();
    }

    /**
     * Test if the translation task correctly excludes headers that contain generic parts (names) from the text.
     *
     * @throws Exception
     */
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test
    public void testHeaderExclusion2() throws Exception {
        service = createMock(ExecutorService.class);
        provider = createMock(TranslationProvider.class);
        callback = createMock(TranslatorCallback.class);
        replayAll();

        Callable task = new TranslateTask(service, provider, TranslationDirection.EnglishToGerman,
                "Somebody says: ALL YOUR BASE ARE BELONG TO US.",
                callback);
        verifyAll();
        resetAll();

        // until now nothing should happen to ensure that the creation of the task is very cheap
        // the next thing is calling the task and that should actually do things.
        Future<String> translationFuture = createMock(Future.class);
        expect(translationFuture.get()).andReturn("ALL DEINE STÜTZPUNKT SIND GEHÖREN UNS.").anyTimes();
        TranslateSentenceTask sentenceTask = PowerMock.createMock(TranslateSentenceTask.class);
        PowerMock.expectStrictNew(TranslateSentenceTask.class,
                new Class<?>[]{TranslationProvider.class, TranslationDirection.class, String.class},
                provider, TranslationDirection.EnglishToGerman, "ALL YOUR BASE ARE BELONG TO US.")
                .andReturn(sentenceTask);
        expect(service.submit(sentenceTask)).andReturn(translationFuture);

        callback.sendTranslation("Somebody says: ALL DEINE STÜTZPUNKT SIND GEHÖREN UNS.");

        PowerMock.replay(sentenceTask);
        PowerMock.replay(TranslateSentenceTask.class);
        replayAll();
        // Everything we expect is recorded. Let's do this.

        task.call();

        PowerMock.verify(sentenceTask);
        PowerMock.verify(TranslateSentenceTask.class);
        verifyAll();
    }

    /**
     * Test if the translation task correctly identifies and excludes OOC marks from the translation.
     *
     * @throws Exception
     */
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test
    public void testHeaderExclusionOoc() throws Exception {
        service = createMock(ExecutorService.class);
        provider = createMock(TranslationProvider.class);
        callback = createMock(TranslatorCallback.class);
        replayAll();

        Callable task = new TranslateTask(service, provider, TranslationDirection.EnglishToGerman,
                "Somebody says: ((ALL YOUR BASE ARE BELONG TO US.))",
                callback);
        verifyAll();
        resetAll();

        // until now nothing should happen to ensure that the creation of the task is very cheap
        // the next thing is calling the task and that should actually do things.
        Future<String> translationFuture = createMock(Future.class);
        expect(translationFuture.get()).andReturn("ALL DEINE STÜTZPUNKT SIND GEHÖREN UNS.").anyTimes();
        TranslateSentenceTask sentenceTask = PowerMock.createMock(TranslateSentenceTask.class);
        PowerMock.expectStrictNew(TranslateSentenceTask.class,
                new Class<?>[]{TranslationProvider.class, TranslationDirection.class, String.class},
                provider, TranslationDirection.EnglishToGerman, "ALL YOUR BASE ARE BELONG TO US.")
                .andReturn(sentenceTask);
        expect(service.submit(sentenceTask)).andReturn(translationFuture);

        callback.sendTranslation("Somebody says: ((ALL DEINE STÜTZPUNKT SIND GEHÖREN UNS.))");

        PowerMock.replay(sentenceTask);
        PowerMock.replay(TranslateSentenceTask.class);
        replayAll();
        // Everything we expect is recorded. Let's do this.

        task.call();

        PowerMock.verify(sentenceTask);
        PowerMock.verify(TranslateSentenceTask.class);
        verifyAll();
    }

    @BeforeMethod
    public void prepareTests() {
        resetAll();
        PowerMock.reset(TranslateSentenceTask.class);
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
