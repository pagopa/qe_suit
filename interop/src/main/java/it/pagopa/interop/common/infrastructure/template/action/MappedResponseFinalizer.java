package it.pagopa.interop.common.infrastructure.template.action;

import it.pagopa.interop.common.kernel.context.EntityStore;
import it.pagopa.interop.common.infrastructure.response.RawResponse;

import java.util.function.Function;

public class MappedResponseFinalizer<SourceResponse, TargetResponse> implements ResponseFinalizer<TargetResponse> {

    private final ResponseFinalizer<SourceResponse> source;
    private final Function<? super SourceResponse, ? extends TargetResponse> mapper;

    public MappedResponseFinalizer(ResponseFinalizer<SourceResponse> source, Function<? super SourceResponse, ? extends TargetResponse> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public <NextResponse> ResponseFinalizer<NextResponse> map(Function<? super TargetResponse, ? extends NextResponse> nextMapper) {
        return new MappedResponseFinalizer<>(
                source,
                sourceResponse -> nextMapper.apply(mapper.apply(sourceResponse))
        );
    }

    @Override
    public EntityStore getEntityStore() {
        return source.getEntityStore();
    }

    @Override
    public TargetResponse get() {
        return mapper.apply(source.get());
    }

    @Override
    public RawResponse getRaw() {
        return source.getRaw();
    }
}