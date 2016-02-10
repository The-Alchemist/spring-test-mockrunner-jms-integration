
import java.lang.annotation.Annotation;

import javax.annotation.Resource;
import javax.jms.Queue;
import javax.jms.Topic;

import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.SimpleAutowireCandidateResolver;
import org.springframework.core.annotation.AnnotationUtils;

import com.mockrunner.jms.DestinationManager;

public class JmsResourceCandidateResolver extends SimpleAutowireCandidateResolver {

    private final DestinationManager mockJmsDestinationManager;

    public JmsResourceCandidateResolver(DestinationManager mockJmsDestinationManager) {
        this.mockJmsDestinationManager = mockJmsDestinationManager;
    }

    @Override
    public Object getSuggestedValue(DependencyDescriptor descriptor) {
        String dependencyClassName = descriptor.getDependencyType().getName();
        if(descriptor.getField() != null) {
            Annotation[] annotations = descriptor.getAnnotations();
            // support Queue objects
            if (dependencyClassName.equals(Queue.class.getName())) {
                String queueName = getJmsQueueName(annotations);
                if(queueName == null) {
                    throwException(dependencyClassName);
                } else {
                    return mockJmsDestinationManager.createQueue(queueName);
                }
            }
            // and Topic objects
            else if(dependencyClassName.equals(Topic.class.getName())) {
                String queueName = getJmsQueueName(annotations);
                if(queueName == null) {
                    throwException(dependencyClassName);
                } else {
                    return mockJmsDestinationManager.createTopic(queueName);
                }
            }
        }

        return super.getSuggestedValue(descriptor);
    }


    private void throwException(String className) {
        throw new IllegalStateException(String.format("Missing @Resource(lookup=...) annotation; Cannot inject a %s because I do not know what name to give it", className));
    }

    private String getJmsQueueName(Annotation[] annotations) {
        for (Annotation ann : annotations) {
            Resource resource = AnnotationUtils.getAnnotation(ann, Resource.class);
            if(resource.lookup() == null && resource.lookup().isEmpty()) {
                throw new IllegalStateException("Could not inject a Queue because the @Resource annotation was missing a @Resource(lookup=....)");
            } else {
                return resource.name();
            }
        }
        return null;
    }

}
