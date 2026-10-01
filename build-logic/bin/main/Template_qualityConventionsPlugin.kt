/**
 * Precompiled [template.quality-conventions.gradle.kts][Template_quality_conventions_gradle] script plugin.
 *
 * @see Template_quality_conventions_gradle
 */
public
class Template_qualityConventionsPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Template_quality_conventions_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
