package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;

import java.util.function.Function;

public class MappedApiFinalizer<SourceResponse, TargetResponse, Model extends Identifiable> implements ApiFinalizer<TargetResponse, Model> {

    private final ApiFinalizer<SourceResponse, Model> source;
    private final Function<? super SourceResponse, ? extends TargetResponse> mapper;

    public MappedApiFinalizer(ApiFinalizer<SourceResponse, Model> source, Function<? super SourceResponse, ? extends TargetResponse> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public <NextResponse> ApiFinalizer<NextResponse, Model> map(Function<? super TargetResponse, ? extends NextResponse> nextMapper) {
        return new MappedApiFinalizer<>(
                source,
                sourceResponse -> nextMapper.apply(mapper.apply(sourceResponse))
        );
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