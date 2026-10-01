/**
 * Precompiled [template.spotless-conventions.gradle.kts][Template_spotless_conventions_gradle] script plugin.
 *
 * @see Template_spotless_conventions_gradle
 */
public
class Template_spotlessConventionsPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Template_spotless_conventions_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
