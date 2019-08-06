package org.kl.pattern;

import org.kl.bean.Edge;
import org.kl.lambda.Provider;
import org.kl.lambda.Purchaser;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class SequencePattern {
    private static Collection range;
    private static Map map;

    private static SequencePattern instance;

    private SequencePattern() {}

    public static <E> void init(Collection<E> other) {
        range = other;
    }

    public static <K, V> void init(Map<K, V> other) {
        map = other;
    }

    public static BooleanSupplier empty() {
        return () -> range.isEmpty();
    }

    public static BooleanSupplier emptyMap() {
        return () -> map.isEmpty();
    }

    public static <E> Supplier<E> head() {
        return SequencePattern::first;
    }

    public static <K, V> Supplier<Map.Entry<K, V>> headMap() {
        return SequencePattern::firstMap;
    }

    public static <E> Supplier<E> middle() {
        return SequencePattern::half;
    }

    public static <K, V> Supplier<Map.Entry<K, V>> middleMap() {
        return SequencePattern::halfMap;
    }

    public static <E> Supplier<E> tail() {
        return SequencePattern::last;
    }

    public static <K, V> Supplier<Map.Entry<K, V>> tailMap() {
        return SequencePattern::lastMap;
    }

    public static <E> Supplier<E> at(int index) {
        return () -> (E) range.toArray()[index];
    }

    public static <K, V> Supplier<Map.Entry<K, V>> atMap(int index) {
        Set<Map.Entry<K, V>> entries = map.entrySet();

        return () -> entries.stream()
                .skip(index)
                .findFirst().get();
    }

    public static <E> Provider<Edge<E>> edges() {
        Edge<E> unique = new Edge<>(first(), last());
        return () ->  unique;
    }

    public static <K, V> Provider<Edge<Map.Entry<K, V>>> edgesMap() {
        Edge<Map.Entry<K, V>> unique = new Edge<>(firstMap(), lastMap());
        return () -> unique;
    }

    public static <E> Provider<Stream<E>> sub(int beginIndex, int endIndex) {
        Stream result = range.stream()
                .skip(beginIndex)
                .limit(endIndex - beginIndex + 1);
        return () -> (Stream<E>) result;
    }

    public static <K, V> Provider<Stream<Map.Entry<K, V>>> subMap(int beginIndex, int endIndex) {
        Stream result = map.entrySet().stream()
                .skip(beginIndex)
                .limit(endIndex - beginIndex + 1);
        return () -> (Stream<Map.Entry<K, V>>) result;
    }

    public static <E> Provider<Stream<E>> some(int... indexes) {
        Stream<E> result = Stream.empty();
        E[] array = (E[]) range.toArray();

        for (int index : indexes) {
            result = Stream.concat(result, Stream.of(array[index]));
        }

        Stream<E> finalResult = result;

        return () -> finalResult;
    }

    public static <K, V> Provider<Stream<Map.Entry<K, V>>> someMap(int... indexes) {
        Stream<Map.Entry<K, V>> result = Stream.empty();
        List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(map.entrySet());

        for (int index : indexes) {
            result = Stream.concat(result, Stream.of(list.get(index)));
        }

        Stream<Map.Entry<K, V>> finalResult = result;

        return () -> finalResult;
    }

    public static <E> Provider<Stream<E>> rest(int beginIndex) {
        Stream result = range.stream()
                .skip(beginIndex);
        return () -> (Stream<E>) result;
    }

    public static <K, V> Provider<Stream<Map.Entry<K, V>>> restMap(int beginIndex) {
        Stream result = map.entrySet().stream()
                .skip(beginIndex);
        return () -> (Stream<Map.Entry<K, V>>) result;
    }

    private static <E> E first() {
        Stream<E> stream = range.stream();

        return stream.findFirst().get();
    }

    private static <K, V> Map.Entry<K, V> firstMap() {
        Set<Map.Entry<K, V>> entries = map.entrySet();

        return entries.stream()
                .findFirst().get();
    }

    private static <E> E half() {
        Stream<E> stream = range.stream();
        long count = range.size() / 2;

        return stream.skip(count - 1)
                .findFirst().get();
    }

    private static <K, V> Map.Entry<K, V> halfMap() {
        Set<Map.Entry<K, V>> entries = map.entrySet();
        long count = map.size() / 2;

        return entries.stream()
                .skip(count - 1)
                .findFirst().get();
    }

    private static <E> E last() {
        Stream<E> stream = range.stream();
        long count = range.size();

        return stream.skip(count - 1)
                .findFirst().get();
    }

    private static <K, V> Map.Entry<K, V> lastMap() {
        Set<Map.Entry<K, V>> entries = map.entrySet();
        long count = range.size();

        return entries.stream()
                .skip(count - 1)
                .findFirst().get();
    }

    public static <E> SequencePattern matches(Collection<E> data) {
        if (!data.isEmpty()) {
            init(data);
        }

        if (instance == null) {
            instance = new SequencePattern();
        }

        return instance;
    }

    public static <K, V> SequencePattern matches(Map<K, V> data) {
        if (!data.isEmpty()) {
            init(data);
        }

        if (instance == null) {
            instance = new SequencePattern();
        }

        return instance;
    }

    public static <E> void matches(Collection<E> data, BooleanSupplier supplier, Runnable branch) {
        if (!data.isEmpty()) {
            init(data);
        }

        if (supplier.getAsBoolean()) {
            branch.run();
        }
    }

    public static <E> void matches(Collection<E> data,
                                   BooleanSupplier firstSupplier, Runnable firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch) {
        matches(data, firstSupplier, firstBranch);
        secondBranch.accept(secondSupplier.get());
    }

    public static <E> void matches(Collection<E> data,
                                   BooleanSupplier firstSupplier, Runnable firstBranch,
                                   Provider<Edge<E>> secondSupplier, BiConsumer<E, E> secondBranch) {
        matches(data, firstSupplier, firstBranch);
        secondBranch.accept(secondSupplier.take().getFirst(), secondSupplier.take().getLast());
    }

    public static <E> void matches(Collection<E> data,
                                   BooleanSupplier firstSupplier, Runnable firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.get());
    }

    public static <E> void matches(Collection<E> data,
                                   BooleanSupplier firstSupplier, Runnable firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Provider<Edge<E>> thirdSupplier, BiConsumer<E, E> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.take().getFirst(), thirdSupplier.take().getLast());
    }

    public static <E> void matches(Collection<E> data,
                                   BooleanSupplier firstSupplier, Runnable firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                                   Supplier<E> fourthSupplier, Consumer<E> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.get());
    }

    public static <E> void matches(Collection<E> data,
                                   BooleanSupplier firstSupplier, Runnable firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                                   Provider<Edge<E>> fourthSupplier, BiConsumer<E, E> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.take().getFirst(), fourthSupplier.take().getLast());
    }

    public static <E> void matches(Collection<E> data,
                                   BooleanSupplier firstSupplier, Runnable firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                                   Supplier<E> fourthSupplier, Consumer<E> fourthBranch,
                                   Supplier<E> fifthSupplier,  Consumer<E> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.get());
    }

    public static <E> void matches(Collection<E> data,
                                   BooleanSupplier firstSupplier, Runnable firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                                   Supplier<E> fourthSupplier, Consumer<E> fourthBranch,
                                   Provider<Edge<E>> fifthSupplier, BiConsumer<E, E> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.take().getFirst(), fifthSupplier.take().getLast());
    }

    public static <E> void matches(Collection<E> data,
                                   BooleanSupplier firstSupplier, Runnable firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                                   Supplier<E> fourthSupplier, Consumer<E> fourthBranch,
                                   Supplier<E> fifthSupplier,  Consumer<E> fifthBranch,
                                   Provider<Edge<E>> sixSupplier, BiConsumer<E, E> sixthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch,
                fifthSupplier, fifthBranch);
        sixthBranch.accept(sixSupplier.take().getFirst(), sixSupplier.take().getLast());
    }

    public void as(BooleanSupplier supplier, Runnable branch) {
        if (supplier.getAsBoolean()) {
            branch.run();
        }
    }

    public <E> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch) {
        as(firstSupplier, firstBranch);
        secondBranch.accept(secondSupplier.get());
    }

    public <E> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Supplier<E> thirdSupplier,  Consumer<E> thirdBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.get());
    }

    public <E> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                       Supplier<E> fourthSupplier, Consumer<E> fourthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.get());
    }

    public <E> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                       Supplier<E> fourthSupplier, Consumer<E> fourthBranch,
                       Supplier<E> fifthSupplier,  Consumer<E> fifthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch,
           fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.get());
    }

    public <E> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                       Provider<Edge<E>> secondSupplier, BiConsumer<E, E> secondBranch) {
        as(firstSupplier, firstBranch);
        secondBranch.accept(secondSupplier.take().getFirst(), secondSupplier.take().getLast());
    }

    public <E> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Provider<Edge<E>> thirdSupplier, BiConsumer<E, E> thirdBranch) {
        as(firstSupplier,  firstBranch,
          secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.take().getFirst(), thirdSupplier.take().getLast());
    }

    public <E> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                       Provider<Edge<E>> fourthSupplier, BiConsumer<E, E> fourthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.take().getFirst(), fourthSupplier.take().getLast());
    }

    public <E> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                       Supplier<E> fourthSupplier, Consumer<E> fourthBranch,
                       Provider<Edge<E>> fifthSupplier, BiConsumer<E, E> fifthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch,
           fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.take().getFirst(), fifthSupplier.take().getLast());
    }

    public <E> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                       Supplier<E> fourthSupplier, Consumer<E> fourthBranch,
                       Supplier<E> fifthSupplier,  Consumer<E> fifthBranch,
                       Provider<Edge<E>> sixSupplier, BiConsumer<E, E> sixthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch,
           fourthSupplier, fourthBranch,
           fifthSupplier, fifthBranch);
        sixthBranch.accept(sixSupplier.take().getFirst(), sixSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data, BooleanSupplier supplier, Runnable branch) {
        if (!data.isEmpty()) {
            init(data);
        }

        if (supplier.getAsBoolean()) {
            branch.run();
        }
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch) {
        matches(data, firstSupplier, firstBranch);
        secondBranch.accept(secondSupplier.get());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Provider<Edge<Map.Entry<K, V>>> secondSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> secondBranch) {
        matches(data, firstSupplier, firstBranch);
        secondBranch.accept(secondSupplier.take().getFirst(), secondSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.get());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Provider<Edge<Map.Entry<K, V>>> thirdSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.take().getFirst(), thirdSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, Consumer<Map.Entry<K, V>> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.get());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch,
                                      Provider<Edge<Map.Entry<K, V>>> fourthSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.take().getFirst(), fourthSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, Consumer<Map.Entry<K, V>> fourthBranch,
                                      Supplier<Map.Entry<K, V>> fifthSupplier,  Consumer<Map.Entry<K, V>> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.get());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, Consumer<Map.Entry<K, V>> fourthBranch,
                                      Provider<Edge<Map.Entry<K, V>>> fifthSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.take().getFirst(), fifthSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, Consumer<Map.Entry<K, V>> fourthBranch,
                                      Supplier<Map.Entry<K, V>> fifthSupplier,  Consumer<Map.Entry<K, V>> fifthBranch,
                                      Supplier<Map.Entry<K, V>> sixthSupplier,  Consumer<Map.Entry<K, V>> sixthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch,
                fifthSupplier,  fifthBranch);
        sixthBranch.accept(sixthSupplier.get());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, Consumer<Map.Entry<K, V>> fourthBranch,
                                      Supplier<Map.Entry<K, V>> fifthSupplier,  Consumer<Map.Entry<K, V>> fifthBranch,
                                      Provider<Edge<Map.Entry<K, V>>> sixSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> sixBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch,
                fifthSupplier, fifthBranch);
        sixBranch.accept(sixSupplier.take().getFirst(), sixSupplier.take().getLast());
    }


    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch) {
        matches(data, firstSupplier, firstBranch);
        secondBranch.accept(secondSupplier.get().getKey(), secondSupplier.get().getValue());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.get().getKey(), thirdSupplier.get().getValue());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Provider<Edge<Map.Entry<K, V>>> thirdSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.take().getFirst(), thirdSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.get().getKey(), fourthSupplier.get().getValue());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                                      Provider<Edge<Map.Entry<K, V>>> fourthSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.take().getFirst(), fourthSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch,
                                      Supplier<Map.Entry<K, V>> fifthSupplier,  BiConsumer<K, V> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.get().getKey(), fifthSupplier.get().getValue());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch,
                                      Provider<Edge<Map.Entry<K, V>>> fifthSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.take().getFirst(), fifthSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch,
                                      Supplier<Map.Entry<K, V>> fifthSupplier,  BiConsumer<K, V> fifthBranch,
                                      Supplier<Map.Entry<K, V>> sixthSupplier,  BiConsumer<K, V> sixthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch,
                fifthSupplier,  fifthBranch);
        sixthBranch.accept(sixthSupplier.get().getKey(), sixthSupplier.get().getValue());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      BooleanSupplier firstSupplier, Runnable firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch,
                                      Supplier<Map.Entry<K, V>> fifthSupplier,  BiConsumer<K, V> fifthBranch,
                                      Provider<Edge<Map.Entry<K, V>>> sixSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> sixBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch,
                fifthSupplier,  fifthBranch);
        sixBranch.accept(sixSupplier.take().getFirst(), sixSupplier.take().getLast());
    }

    public <K, V> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch) {
        as(firstSupplier, firstBranch);
        secondBranch.accept(secondSupplier.get().getKey(), secondSupplier.get().getValue());
    }

    public <K, V> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.get().getKey(), thirdSupplier.get().getValue());
    }

    public <K, V> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                          Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.get().getKey(), fourthSupplier.get().getValue());
    }

    public <K, V> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                          Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch,
                          Supplier<Map.Entry<K, V>> fifthSupplier,  BiConsumer<K, V> fifthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch,
           fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.get().getKey(), fifthSupplier.get().getValue());
    }


    public <K, V> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Provider<Edge<Map.Entry<K, V>>> thirdSupplier,
                          BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> thirdBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.take().getFirst(), thirdSupplier.take().getLast());
    }

    public <K, V> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                          Provider<Edge<Map.Entry<K, V>>> fourthSupplier,
                          BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fourthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.take().getFirst(), fourthSupplier.take().getLast());
    }

    public <K, V> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                          Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch,
                          Provider<Edge<Map.Entry<K, V>>> fifthSupplier,
                          BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fifthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch,
           fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.take().getFirst(), fifthSupplier.take().getLast());
    }


    public <K, V> void as(BooleanSupplier firstSupplier, Runnable firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                          Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch,
                          Supplier<Map.Entry<K, V>> fifthSupplier,  BiConsumer<K, V> fifthBranch,
                          Provider<Edge<Map.Entry<K, V>>> sixSupplier,
                          BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> sixBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch,
           fourthSupplier, fourthBranch,
           fifthSupplier,  fifthBranch);
        sixBranch.accept(sixSupplier.take().getFirst(), sixSupplier.take().getLast());
    }

    public static <E> void matches(Collection<E> data, Supplier<E> supplier, Consumer<E> branch) {
        if (!data.isEmpty()) {
            init(data);

            branch.accept(supplier.get());
        }
    }

    public static <E> void matches(Collection<E> data,
                                   Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch) {
        matches(data, firstSupplier,  firstBranch);
        secondBranch.accept(secondSupplier.get());
    }

    public static <E> void matches(Collection<E> data,
                                   Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                                   Provider<Edge<E>> secondSupplier, BiConsumer<E, E> secondBranch) {
        matches(data, firstSupplier,  firstBranch);
        secondBranch.accept(secondSupplier.take().getFirst(), secondSupplier.take().getLast());
    }

    public static <E> void matches(Collection<E> data,
                                   Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.get());
    }

    public static <E> void matches(Collection<E> data,
                                   Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Provider<Edge<E>> thirdSupplier, BiConsumer<E, E> thirdBranch) {
        matches(data,
                firstSupplier, firstBranch,
                secondSupplier,  secondBranch);
        thirdBranch.accept(thirdSupplier.take().getFirst(), thirdSupplier.take().getLast());
    }

    public static <E> void matches(Collection<E> data,
                                   Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                                   Supplier<E> fourthSupplier, Consumer<E> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.get());
    }

    public static <E> void matches(Collection<E> data,
                                   Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                                   Provider<Edge<E>> fourthSupplier, BiConsumer<E, E> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.take().getFirst(), fourthSupplier.take().getLast());
    }

    public static <E> void matches(Collection<E> data,
                                   Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                                   Supplier<E> fourthSupplier, Consumer<E> fourthBranch,
                                   Supplier<E> fifthSupplier,  Consumer<E> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.get());
    }

    public static <E> void matches(Collection<E> data,
                                   Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                                   Supplier<E> secondSupplier, Consumer<E> secondBranch,
                                   Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                                   Supplier<E> fourthSupplier, Consumer<E> fourthBranch,
                                   Provider<Edge<E>> fifthSupplier, BiConsumer<E, E> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.take().getFirst(), fifthSupplier.take().getLast());
    }

    public <E> void as(Supplier<E> supplier, Consumer<E> branch) {
        branch.accept(supplier.get());
    }

    public <E> void as(Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch) {
        as(firstSupplier,  firstBranch);
        secondBranch.accept(secondSupplier.get());
    }

    public <E> void as(Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Supplier<E> thirdSupplier,  Consumer<E> thirdBranch) {
        as(firstSupplier,  firstBranch,
          secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.get());
    }

    public <E> void as(Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                       Supplier<E> fourthSupplier, Consumer<E> fourthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.get());
    }

    public <E> void as(Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                       Provider<Edge<E>> secondSupplier, BiConsumer<E, E> secondBranch) {
        as(firstSupplier,  firstBranch);
        secondBranch.accept(secondSupplier.take().getFirst(), secondSupplier.take().getLast());
    }

    public <E> void as(Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Provider<Edge<E>> thirdSupplier, BiConsumer<E, E> thirdBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.take().getFirst(), thirdSupplier.take().getLast());
    }

    public <E> void as(Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                       Provider<Edge<E>> fourthSupplier, BiConsumer<E, E> fourthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.take().getFirst(), fourthSupplier.take().getLast());
    }

    public <E> void as(Supplier<E> firstSupplier,  Consumer<E> firstBranch,
                       Supplier<E> secondSupplier, Consumer<E> secondBranch,
                       Supplier<E> thirdSupplier,  Consumer<E> thirdBranch,
                       Supplier<E> fourthSupplier, Consumer<E> fourthBranch,
                       Provider<Edge<E>> fifthSupplier, BiConsumer<E, E> fifthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch,
           fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.take().getFirst(), fifthSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data, Supplier<Map.Entry<K, V>> supplier, Consumer<Map.Entry<K, V>> branch) {
        if (!data.isEmpty()) {
            init(data);

            branch.accept(supplier.get());
        }
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  Consumer<Map.Entry<K, V>> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch) {
        matches(data, firstSupplier, firstBranch);
        secondBranch.accept(secondSupplier.get());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  Consumer<Map.Entry<K, V>> firstBranch,
                                      Provider<Edge<Map.Entry<K, V>>> secondSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> secondBranch) {
        matches(data, firstSupplier, firstBranch);
        secondBranch.accept(secondSupplier.take().getFirst(), secondSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  Consumer<Map.Entry<K, V>> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.get());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  Consumer<Map.Entry<K, V>> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Provider<Edge<Map.Entry<K, V>>> thirdSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.take().getFirst(), thirdSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  Consumer<Map.Entry<K, V>> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, Consumer<Map.Entry<K, V>> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.get());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  Consumer<Map.Entry<K, V>> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch,
                                      Provider<Edge<Map.Entry<K, V>>> fourthSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.take().getFirst(), fourthSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  Consumer<Map.Entry<K, V>> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, Consumer<Map.Entry<K, V>> fourthBranch,
                                      Supplier<Map.Entry<K, V>> fifthSupplier,  Consumer<Map.Entry<K, V>> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.get());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  Consumer<Map.Entry<K, V>> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, Consumer<Map.Entry<K, V>> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  Consumer<Map.Entry<K, V>> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, Consumer<Map.Entry<K, V>> fourthBranch,
                                      Provider<Edge<Map.Entry<K, V>>> fifthSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.take().getFirst(), fifthSupplier.take().getLast());
    }

    public static <E> void matches(Collection<E> data, Provider<Edge<E>> supplier, BiConsumer<E, E> branch) {
        if (!data.isEmpty()) {
            init(data);

            branch.accept(supplier.take().getFirst(), supplier.take().getLast());
        }
    }

    public <E> void as(Provider<Edge<E>> supplier, BiConsumer<E, E> branch) {
        branch.accept(supplier.take().getFirst(), supplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data, Provider<Edge<Map.Entry<K, V>>> supplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> branch) {
        if (!data.isEmpty()) {
            init(data);

            branch.accept(supplier.take().getFirst(), supplier.take().getLast());
        }
    }

    public static <K, V> void matches(Map<K, V> data, Supplier<Map.Entry<K, V>> supplier, BiConsumer<K, V> branch) {
        if (!data.isEmpty()) {
            init(data);

            branch.accept(supplier.get().getKey(), supplier.get().getValue());
        }
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch) {
        matches(data, firstSupplier,  firstBranch);
        secondBranch.accept(secondSupplier.get().getKey(), secondSupplier.get().getValue());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                                      Provider<Edge<Map.Entry<K, V>>> secondSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> secondBranch) {
        matches(data, firstSupplier,  firstBranch);
        secondBranch.accept(secondSupplier.take().getFirst(), secondSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.get().getKey(), thirdSupplier.get().getValue());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Provider<Edge<Map.Entry<K, V>>> thirdSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> thirdBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.take().getFirst(), thirdSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.get().getKey(), fourthSupplier.get().getValue());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                                      Provider<Edge<Map.Entry<K, V>>> fourthSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fourthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.take().getFirst(), fourthSupplier.take().getLast());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch,
                                      Supplier<Map.Entry<K, V>> fifthSupplier,  BiConsumer<K, V> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.get().getKey(), fifthSupplier.get().getValue());
    }

    public static <K, V> void matches(Map<K, V> data,
                                      Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                                      Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                                      Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                                      Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch,
                                      Provider<Edge<Map.Entry<K, V>>> fifthSupplier,
                                      BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fifthBranch) {
        matches(data,
                firstSupplier,  firstBranch,
                secondSupplier, secondBranch,
                thirdSupplier,  thirdBranch,
                fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.take().getFirst(), fifthSupplier.take().getLast());
    }

    public <K, V> void as(Supplier<Map.Entry<K, V>> supplier, BiConsumer<K, V> branch) {
        branch.accept(supplier.get().getKey(), supplier.get().getValue());
    }

    public <K, V> void as(Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch) {
        as(firstSupplier,  firstBranch);
        secondBranch.accept(secondSupplier.get().getKey(), secondSupplier.get().getValue());
    }

    public <K, V> void as(Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.get().getKey(), thirdSupplier.get().getValue());
    }

    public <K, V> void as(Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                          Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.get().getKey(), fourthSupplier.get().getValue());
    }

    public <K, V> void as(Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                          Provider<Edge<Map.Entry<K, V>>> secondSupplier,
                          BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> secondBranch) {
        as(firstSupplier,  firstBranch);
        secondBranch.accept(secondSupplier.take().getFirst(), secondSupplier.take().getLast());
    }

    public <K, V> void as(Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Provider<Edge<Map.Entry<K, V>>> thirdSupplier,
                          BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> thirdBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch);
        thirdBranch.accept(thirdSupplier.take().getFirst(), thirdSupplier.take().getLast());
    }

    public <K, V> void as(Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                          Provider<Edge<Map.Entry<K, V>>> fourthSupplier,
                          BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fourthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch);
        fourthBranch.accept(fourthSupplier.take().getFirst(), fourthSupplier.take().getLast());
    }

    public <K, V> void as(Supplier<Map.Entry<K, V>> firstSupplier,  BiConsumer<K, V> firstBranch,
                          Supplier<Map.Entry<K, V>> secondSupplier, BiConsumer<K, V> secondBranch,
                          Supplier<Map.Entry<K, V>> thirdSupplier,  BiConsumer<K, V> thirdBranch,
                          Supplier<Map.Entry<K, V>> fourthSupplier, BiConsumer<K, V> fourthBranch,
                          Provider<Edge<Map.Entry<K, V>>> fifthSupplier,
                          BiConsumer<Map.Entry<K, V>, Map.Entry<K, V>> fifthBranch) {
        as(firstSupplier,  firstBranch,
           secondSupplier, secondBranch,
           thirdSupplier,  thirdBranch,
           fourthSupplier, fourthBranch);
        fifthBranch.accept(fifthSupplier.take().getFirst(), fifthSupplier.take().getLast());
    }

    public static <E> void matches(Collection<E> data, Provider<Stream<E>> supplier, Purchaser<Stream<E>> branch) {
        if (!data.isEmpty()) {
            init(data);

            branch.obtain(supplier.take());
        }
    }

    public <E> void as(Provider<Stream<E>> supplier, Purchaser<Stream<E>> branch) {
        branch.obtain(supplier.take());
    }

    public static <K, V> void matches(Map<K, V> data, Provider<Stream<Map.Entry<K, V>>> supplier, Purchaser<Stream<Map.Entry<K, V>>> branch) {
        if (!data.isEmpty()) {
            init(data);

            branch.obtain(supplier.take());
        }
    }
}
