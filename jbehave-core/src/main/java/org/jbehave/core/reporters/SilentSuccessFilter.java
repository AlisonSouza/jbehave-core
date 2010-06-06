package org.jbehave.core.reporters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.OutcomesTable;
import org.jbehave.core.model.Story;

/**
 * Filters out the reports from all stories that pass,
 * The delegate receives output only for failing or pending stories.
 */
public class SilentSuccessFilter implements StoryReporter {

    private final StoryReporter delegate;
    private List<Todo> currentScenario = new ArrayList<Todo>();
    private State scenarioState = State.SILENT;
    private State beforeStoryState = State.SILENT;
    private State afterStoryState = State.SILENT;
    private boolean givenStory;

    public SilentSuccessFilter(StoryReporter delegate) {
        this.delegate = delegate;
    }

    public void afterStory(boolean givenStory) {
        afterStoryState.report();
    }

    public void beforeStory(final Story story, final boolean givenStory) {
        this.givenStory = givenStory;
        beforeStoryState = new State() {
            public void report() {
                delegate.beforeStory(story, givenStory);
                beforeStoryState = State.SILENT;
            }
        };
    }

    public void ignorable(final String step) {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.ignorable(step);
            }
        });
    }

    public void failed(final String step, final Throwable cause) {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.failed(step, cause);
            }
        });
        setStateToNoisy();
    }

    public void failedOutcomes(final String step, final OutcomesTable table) {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.failedOutcomes(step, table);
            }
        });
        setStateToNoisy();
    }

    public void notPerformed(final String step) {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.notPerformed(step);
            }
        });
    }

    public void pending(final String step) {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.pending(step);
            }
        });
        setStateToNoisy();
    }

    private void setStateToNoisy() {
        scenarioState = new State() {
            public void report() {
                beforeStoryState.report();
                for (Todo todo : currentScenario) {
                    todo.doNow();
                }
                afterStoryState = new State() {
                    public void report() {
                        delegate.afterStory(givenStory);
                        afterStoryState = State.SILENT;
                    }
                };
                scenarioState = State.SILENT;
            }
        };
    }

    public void successful(final String step) {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.successful(step);
            }
        });
    }

    public void afterScenario() {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.afterScenario();
            }
        });
        scenarioState.report();
    }

    public void beforeScenario(final String title) {
        currentScenario = new ArrayList<Todo>();
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.beforeScenario(title);
            }
        });
    }

	public void givenStories(final List<String> storyPaths) {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.givenStories(storyPaths);
            }
        });
	}
	
	public void beforeExamples(final List<String> steps, final ExamplesTable table) {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.beforeExamples(steps, table);
            }
        });		
	}

	public void example(final Map<String, String> tableRow) {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.example(tableRow);
            }
        });		
	}
	
    public void afterExamples() {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.afterExamples();
            }
        });     
    }

    public void dryRun() {
        currentScenario.add(new Todo() {
            public void doNow() {
                delegate.dryRun();
            }
        });
    }
    
    private static interface Todo {
        void doNow();
    }

    private interface State {
        State SILENT = new State() {
            public void report() {
            }
        };

        void report();
    }

}
