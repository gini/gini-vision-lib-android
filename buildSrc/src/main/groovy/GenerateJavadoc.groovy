import org.gradle.api.Task
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.javadoc.Javadoc

class GenerateJavadoc extends Javadoc {

    @Input
    String projectTitle = "Unknown Title"

    @Override
    Task configure(final Closure closure) {
        applyDefaultConfiguration()

        def configuration = super.configure(closure)

        options.windowTitle = projectTitle
        options.docTitle = projectTitle

        doLast {
            project.copy {
                from "src/main/javadoc"
                into "$project.buildDir/docs/javadoc"
            }
        }
        return configuration
    }

    private void applyDefaultConfiguration() {
        source = project.fileTree("src/main/java/").files.grep { it.name.endsWith('.java') }
        exclude '**/BuildConfig.java'
        exclude '**/R.java'
        exclude '**/TouchImageView.java'
        failOnError = false
        options.showFromPublic()
        List pathList = new ArrayList();
        pathList.add(project.file("${project.rootDir}/tools/excludedoclet/doclet.jar"));
        options.docletpath = pathList
        options.doclet = "ExcludeDoclet"
        options.version = true
        options.author = true
        options.overview = project.fileTree("src/main/java/").files.find {
            (it.name == 'overview.html')
        }
    }

}