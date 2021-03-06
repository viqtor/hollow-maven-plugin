package com.github.igorperikov.hollow;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Mojo(name = "generate-as-project-sources")
public class SingleModuleHollowMojo extends AbstractMojo {

    @Parameter(property = "packagesToScan", required = true)
    public List<String> packagesToScan;

    @Parameter(property = "apiClassName", required = true)
    public String apiClassName;

    @Parameter(property = "apiPackageName", required = true)
    public String apiPackageName;

    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        HollowAPIGenerator generator = HollowAPIGeneratorUtility.createHollowAPIGenerator(
                project,
                packagesToScan,
                apiClassName,
                apiPackageName,
                getLog()
        );

        String javaSourcesPath = project.getBasedir().getAbsolutePath() + "/src/main/java/";
        String apiTargetFolderPath = ApiTargetFolderUtility.buildPathToApiTargetFolder(apiPackageName, javaSourcesPath);

        cleanupAndCreateFolders(apiTargetFolderPath);
        try {
            generator.generateFiles(apiTargetFolderPath);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to generate consumer api", e);
        }
    }

    private void cleanupAndCreateFolders(String generatedApiTarget) {
        File apiCodeFolder = new File(generatedApiTarget);
        apiCodeFolder.mkdirs();
        for (File f : apiCodeFolder.listFiles()) {
            f.delete();
        }
    }
}
