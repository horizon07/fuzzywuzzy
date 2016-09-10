package com.xdrop.fuzzywuzzy;

import com.xdrop.diffutils.DiffUtils;
import com.xdrop.diffutils.structs.MatchingBlock;
import com.xdrop.fuzzywuzzy.ratios.PartialRatio;
import com.xdrop.fuzzywuzzy.ratios.SimpleRatio;
import org.apache.commons.lang.StringUtils;

import java.util.*;

@SuppressWarnings("WeakerAccess")
public class FuzzySearch {


    /**
     * Calculates a Levenshtein simple ratio between the strings.
     * This is indicates a measure of similarity
     *
     * @param s1 Input string
     * @param s2 Input string
     * @return The simple ratio
     */
    public static int ratio(String s1, String s2) {

        return new SimpleRatio().apply(s1, s2);

    }

    /**
     * Returns a partial ratio.
     *
     * @param s1 Input string
     * @param s2 Input string
     * @return The partial ratio
     */
    public static int partialRatio(String s1, String s2) {

        return new PartialRatio().apply(s1, s2);

    }

    private String processAndSort(String in) {

        in = Utils.processString(in, false);
        String[] wordsArray = in.split("\\s+");

        List<String> words = Arrays.asList(wordsArray);
        String joined = Utils.sortAndJoin(words, " ");

        return joined.trim();

    }

    /**
     * Find all alphanumeric tokens in the string and sort
     * those tokens and then take ratio of resulting
     * joined strings.
     *
     * @param s1
     * @param s2
     * @param partial Whether to apply partial ratio or not
     * @return The ratio of the strings
     */
    private int tokenSort(String s1, String s2, boolean partial) {

        String sorted1 = processAndSort(s1);
        String sorted2 = processAndSort(s2);

        return partial ? partialRatio(sorted1, sorted2) : ratio(sorted1, sorted2);

    }

    /**
     * Find all alphanumeric tokens in the string and sort
     * those tokens and then take ratio of resulting
     * joined strings.
     *
     * @param s1
     * @param s2
     * @return The partial ratio of the strings
     */
    public int tokenSortPartial(String s1, String s2) {

        return tokenSort(s1, s2, true);

    }

    /**
     * Find all alphanumeric tokens in the string and sort
     * those tokens and then take ratio of resulting
     * joined strings.
     *
     * @param s1
     * @param s2
     * @return The full ratio of the strings
     */
    public int tokenSortFull(String s1, String s2) {

        return tokenSort(s1, s2, false);

    }

    private int tokenSet(String s1, String s2, Ratio ratio) {

        s1 = Utils.processString(s1, false);
        s2 = Utils.processString(s2, false);

        Set<String> tokens1 = Utils.tokenizeSet(s1);
        Set<String> tokens2 = Utils.tokenizeSet(s2);

        Set<String> intersection = SetUtils.intersection(tokens1, tokens2);
        Set<String> diff1to2 = SetUtils.difference(tokens1, tokens2);
        Set<String> diff2to1 = SetUtils.difference(tokens2, tokens1);

        String sortedInter = Utils.sortAndJoin(intersection, " ").trim();
        String sorted1to2 = (sortedInter + " " + Utils.sortAndJoin(diff1to2, " ")).trim();
        String sorted2to1 = (sortedInter + " " + Utils.sortAndJoin(diff2to1, " ")).trim();


        List<Integer> results = new ArrayList<>();

        results.add(ratio.apply(sortedInter, sorted1to2));
        results.add(ratio.apply(sortedInter, sorted2to1));
        results.add(ratio.apply(sorted1to2, sorted2to1));


        return Collections.max(results);

    }

    public int tokenSetRatio(String s1, String s2) {

        return tokenSet(s1, s2, new SimpleRatio());

    }

    public int tokenSetPartial(String s1, String s2) {

        return tokenSet(s1, s2, new PartialRatio());

    }




}
