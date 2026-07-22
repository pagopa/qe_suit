package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.DomainContext;
import it.pagopa.interop.new_arch.common.infrastructure.response.ApiResponse;

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
    public DomainContext getDomainContext() {
        return source.getDomainContext();
    }

    @Override
    public TargetResponse get() {
        return mapper.apply(source.get());
    }

    @Override
    public ApiResponse getRaw() {
        return source.getRaw();
    }
}