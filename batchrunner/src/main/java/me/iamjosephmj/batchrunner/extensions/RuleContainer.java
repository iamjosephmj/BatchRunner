package me.iamjosephmj.batchrunner.extensions;

import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;

public class RuleContainer {
    private final IdentityHashMap<Object, Integer> orderValues = new IdentityHashMap<>();
    private final List<TestRule> testRules = new ArrayList<>();
    private final List<MethodRule> methodRules = new ArrayList<>();

    /**
     * Sets order value for the specified rule.
     */
    public void setOrder(Object rule, int order) {
        orderValues.put(rule, order);
    }

    public void add(MethodRule methodRule) {
        methodRules.add(methodRule);
    }

    public void add(TestRule testRule) {
        testRules.add(testRule);
    }

    static final Comparator<RuleContainer.RuleEntry> ENTRY_COMPARATOR = new Comparator<RuleContainer.RuleEntry>() {
        public int compare(RuleContainer.RuleEntry o1, RuleContainer.RuleEntry o2) {
            int result = compareInt(o1.order, o2.order);
            return result != 0 ? result : o1.type - o2.type;
        }

        private int compareInt(int a, int b) {
            return Integer.compare(b, a);
        }
    };

    /**
     * Returns entries in the order how they should be applied, i.e. inner-to-outer.
     */
    private List<RuleContainer.RuleEntry> getSortedEntries() {
        List<RuleContainer.RuleEntry> ruleEntries = new ArrayList<RuleContainer.RuleEntry>(
                methodRules.size() + testRules.size());
        for (MethodRule rule : methodRules) {
            ruleEntries.add(new RuleContainer.RuleEntry(rule, RuleContainer.RuleEntry.TYPE_METHOD_RULE, orderValues.get(rule)));
        }
        for (TestRule rule : testRules) {
            ruleEntries.add(new RuleContainer.RuleEntry(rule, RuleContainer.RuleEntry.TYPE_TEST_RULE, orderValues.get(rule)));
        }
        Collections.sort(ruleEntries, ENTRY_COMPARATOR);
        return ruleEntries;
    }

    /**
     * Applies all the rules ordered accordingly to the specified {@code statement}.
     */
    public Statement apply(FrameworkMethod method, Description description, Object target,
                           Statement statement) {
        if (methodRules.isEmpty() && testRules.isEmpty()) {
            return statement;
        }
        Statement result = statement;
        for (RuleContainer.RuleEntry ruleEntry : getSortedEntries()) {
            if (ruleEntry.type == RuleContainer.RuleEntry.TYPE_TEST_RULE) {
                result = ((TestRule) ruleEntry.rule).apply(result, description);
            } else {
                result = ((MethodRule) ruleEntry.rule).apply(result, method, target);
            }
        }
        return result;
    }

    /**
     * Returns rule instances in the order how they should be applied, i.e. inner-to-outer.
     * VisibleForTesting
     */
    List<Object> getSortedRules() {
        List<Object> result = new ArrayList<>();
        for (RuleContainer.RuleEntry entry : getSortedEntries()) {
            result.add(entry.rule);
        }
        return result;
    }

    static class RuleEntry {
        static final int TYPE_TEST_RULE = 1;
        static final int TYPE_METHOD_RULE = 0;

        final Object rule;
        final int type;
        final int order;

        RuleEntry(Object rule, int type, Integer order) {
            this.rule = rule;
            this.type = type;
            this.order = order != null ? order : Rule.DEFAULT_ORDER;
        }
    }
}
