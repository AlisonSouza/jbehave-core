package org.jbehave.core.model;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hamcrest.Matcher;

public class OutcomesTable {

    private static final List<String> FIELDS = asList("Description", "Actual", "Matcher", "Verified");
	private static final String NEWLINE = "\n";
    private static final String HEADER_SEPARATOR = "|";
    private static final String VALUE_SEPARATOR = "|";

	private List<Outcome<?>> outcomes = new ArrayList<Outcome<?>>();
	private List<Outcome<?>> failedOutcomes = new ArrayList<Outcome<?>>();
	private OutcomesFailed failureCause;

	public <T> void addOutcome(String description, T actual, Matcher<T> matcher) {
		outcomes.add(new Outcome<T>(description, actual, matcher));
	}

	public void verify() {
		boolean failed = false;
		failedOutcomes.clear();
		for (Outcome<?> outcome : outcomes) {
			if (!outcome.isVerified()) {
				failedOutcomes.add(outcome);
				failed = true;
				break;
			}
		}
		if (failed) {
			failureCause = new OutcomesFailed(this);
			throw failureCause;
		}
	}

	public Throwable failureCause() {
		return failureCause;
	}

	public List<Outcome<?>> getOutcomes() {
		return outcomes;
	}

	public List<Outcome<?>> getFailedOutcomes() {
		return failedOutcomes;
	}

	public List<String> getOutcomeFields(){
		return FIELDS;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> iterator = FIELDS.iterator(); iterator.hasNext(); ) {
			sb.append(HEADER_SEPARATOR).append(iterator.next());
			if (!iterator.hasNext()){
				sb.append(HEADER_SEPARATOR).append(NEWLINE);				
			}
		}
		for (Outcome<?> outcome : outcomes) {
			sb.append(VALUE_SEPARATOR).append(outcome.getDescription())
			  .append(VALUE_SEPARATOR).append(outcome.getActual())
			  .append(VALUE_SEPARATOR).append(outcome.getMatcher())
			  .append(VALUE_SEPARATOR).append(outcome.isVerified())
			  .append(VALUE_SEPARATOR).append(NEWLINE);
		}
		return sb.toString();
	}

	public static class Outcome<T> {

		private final String description;
		private final T actual;
		private final Matcher<T> matcher;
		private final boolean verified;
		
		public Outcome(String description, T actual, Matcher<T> matcher) {
			this.description = description;
			this.actual = actual;
			this.matcher = matcher;
			this.verified = matcher.matches(actual);
		}

		public String getDescription() {
			return description;
		}

		public T getActual() {
			return actual;
		}

		public Matcher<T> getMatcher() {
			return matcher;
		}

		public boolean isVerified() {
			return verified;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this,
					ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

	@SuppressWarnings("serial")
	public static class OutcomesFailed extends RuntimeException {
		private OutcomesTable outcomes;

		public OutcomesFailed(OutcomesTable outcomes) {
			this.outcomes = outcomes;
		}

		public OutcomesTable outcomesTable() {
			return outcomes;
		}

	}

}
