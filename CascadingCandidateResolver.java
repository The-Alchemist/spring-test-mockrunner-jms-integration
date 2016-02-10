
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;

/**
 * Tiny wrapper around {@link AutowireCandidateResolver} that iterates over a list of {@link AutowireCandidateResolver}s and calls {@link #getSuggestedValue(DependencyDescriptor)} on each
 *
 * @author kpietrzak
 *
 */
public class CascadingCandidateResolver implements AutowireCandidateResolver {

    private final Iterable<AutowireCandidateResolver> resolvers;

    public CascadingCandidateResolver(Iterable<AutowireCandidateResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        return bdHolder.getBeanDefinition().isAutowireCandidate();
    }

    /**
     * Return first non-null value
     */
    @Override
    public Object getSuggestedValue(DependencyDescriptor descriptor) {
        for (AutowireCandidateResolver resolver : resolvers) {
            Object value = resolver.getSuggestedValue(descriptor);
            if(value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, String beanName) {
        return null;
    }


}
