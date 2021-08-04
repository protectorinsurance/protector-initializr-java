package no.protector.initializr.extensions

import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo

class SystemTestContainerLoggingExtension implements IAnnotationDrivenExtension<SystemTestContainerLogging> {

    @Override
    void visitSpecAnnotation(SystemTestContainerLogging annotation, SpecInfo spec) {
        //Needs to be present, or else Spock throws an exception
    }

    @Override
    void visitSpec(SpecInfo spec) {
        spec.addListener(new SystemTestErrorListener())
    }
}
