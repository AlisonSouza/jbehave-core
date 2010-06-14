package org.jbehave.core.io;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Abstraction of story location, handling cases in which story path is defined as URL or as resource in classpath.
 */
public class StoryLocation {

    private final String storyPath;
    private URL codeLocation;
    private boolean url;

    public StoryLocation(String storyPath, URL codeLocation) {
        this.storyPath = storyPath;
        this.codeLocation = codeLocation;
        this.url = url();
    }

    public static URL codeLocationFromClass(Class<?> codeLocationClass) {
        return codeLocationClass.getProtectionDomain().getCodeSource().getLocation();
    }

    public String getPath() {
        return storyPath;
    }

    public URL getCodeLocation() {
        return codeLocation;
    }

    public String getLocation() {
        if (url) {
            return storyPath;
        } else {
            return storyURL();
        }
    }

    public String getName() {
        if (url) {
            return storyName();
        } else {
            return storyPath;
        }
    }

    public String storyURL() {
        return codeLocation.getProtocol() + ":" + codeLocation.getFile() + storyPath;
    }

    private String storyName() {
        int codeLocationToStripOff = storyPath.indexOf(codeLocation.getFile()) + codeLocation.getFile().length();
        return storyPath.substring(codeLocationToStripOff, storyPath.length());
    }

    public boolean isURL() {
        return url;
    }

    private boolean url() {
        try {
            new URL(storyPath);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}