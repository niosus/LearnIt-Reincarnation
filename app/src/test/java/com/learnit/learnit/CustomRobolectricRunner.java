package com.learnit.learnit;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;
import org.robolectric.res.FsFile;

import java.io.File;

// If the classes rely on resources from the main part, they will need to use this custom runner
// otherwise they will not see the needed resources, like strings and what not.
public class CustomRobolectricRunner extends RobolectricGradleTestRunner {


    public CustomRobolectricRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    protected AndroidManifest getAppManifest(Config config) {

        FsFile androidManifestFile = Fs.fileFromPath("src/test/AndroidManifest.xml");
        System.out.println(androidManifestFile.getPath());
        FsFile resDirectory = Fs.fileFromPath("src/main/res");
        FsFile assetsDirectory = Fs.newFile(new File("src/test/assets"));
        return new AndroidManifest(androidManifestFile, resDirectory, assetsDirectory) {
            @Override
            public String getRClassName() throws Exception {
                return com.learnit.learnit.R.class.getName();
            }
        };
    }
}