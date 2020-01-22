
package com.epam.gallery.jmhtest;


import com.epam.gallery.config.DatabaseTestConfig;
import com.epam.gallery.repository.AuthorRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;






@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DatabaseTestConfig.class, loader = AnnotationConfigContextLoader.class)
@Transactional
@Rollback
public class DataBaseBenchmark {
    private static final int SET_SIZE = 10;

    private Set<String> hashSet;


    private String stringToFind = "8888";

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void executeJmhRunner() throws RunnerException {

        BufferStorge.setAuthorRepository(authorRepository);

        final Options options = new OptionsBuilder()
                .include(DataBaseBenchmark.class.getSimpleName())
                .forks(0)
                .build();

        new Runner(options).run();
    }



    @Setup
    public void setupBenchmark() {

        System.out.println("-----------------");
        System.out.println(BufferStorge.getAuthorRepository());
        System.out.println("-----------------");

        hashSet = new HashSet<>(SET_SIZE);
        for (int i = 0; i < SET_SIZE; i++) {
            final String value = String.valueOf(i);
            hashSet.add(value);

        }
        stringToFind = String.valueOf(new Random().nextInt(SET_SIZE));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void someBenchmarkMethod(Blackhole blackhole) {

       // blackhole.consume(hashSet.contains(stringToFind));
        blackhole.consume(BufferStorge.getAuthorRepository().findAll());
    }


    static class BufferStorge {

        static private AuthorRepository authorRepository;

        public static AuthorRepository getAuthorRepository() {
            return authorRepository;
        }

        public static void setAuthorRepository(AuthorRepository authorRepository) {
            BufferStorge.authorRepository = authorRepository;
        }
    }


}
