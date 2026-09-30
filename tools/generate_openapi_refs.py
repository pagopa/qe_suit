#!/usr/bin/env python3
"""
Generatore di un file OpenAPI 3.0 con un numero elevato di $ref INTERNI
(riferimenti nella forma '#/components/schemas/...').

Obiettivo: produrre un documento valido che contenga tra 3000 e 5000 $ref interni.

Uso:
    python generate_openapi_refs.py --out openapi-refs.yaml --target 4000

Il numero esatto di $ref generati viene stampato a fine esecuzione e
inserito anche nella description del documento.
"""

import argparse
from collections import OrderedDict

try:
    import yaml
except ImportError as exc:  # pragma: no cover
    raise SystemExit(
        "Il modulo PyYAML non è installato. Installa con: pip install pyyaml"
    ) from exc


# ---------------------------------------------------------------------------
# Rappresentazione ordinata per avere un output YAML leggibile e deterministico
# ---------------------------------------------------------------------------
class _OrderedDumper(yaml.SafeDumper):
    pass


def _dict_representer(dumper, data):
    return dumper.represent_mapping(
        yaml.resolver.BaseResolver.DEFAULT_MAPPING_TAG, data.items()
    )


_OrderedDumper.add_representer(OrderedDict, _dict_representer)


def ref(path: str) -> "OrderedDict":
    """Crea un nodo $ref interno."""
    return OrderedDict({"$ref": f"#/components/schemas/{path}"})


def build_document(target_refs: int) -> "tuple[OrderedDict, int]":
    """
    Costruisce il documento OpenAPI cercando di raggiungere ~target_refs
    riferimenti interni. Ritorna (documento, numero_ref_effettivi).
    """
    ref_count = 0

    schemas: "OrderedDict" = OrderedDict()

    # --- Schemi "foglia" riutilizzabili (non contengono $ref) ----------------
    schemas["Id"] = OrderedDict(
        {"type": "string", "format": "uuid", "description": "Identificatore univoco"}
    )
    schemas["Timestamp"] = OrderedDict({"type": "string", "format": "date-time"})
    schemas["Money"] = OrderedDict(
        {
            "type": "object",
            "properties": OrderedDict(
                {
                    "amount": OrderedDict({"type": "number", "format": "double"}),
                    "currency": OrderedDict({"type": "string", "example": "EUR"}),
                }
            ),
        }
    )
    schemas["ErrorDetail"] = OrderedDict(
        {
            "type": "object",
            "properties": OrderedDict(
                {
                    "code": OrderedDict({"type": "string"}),
                    "message": OrderedDict({"type": "string"}),
                }
            ),
        }
    )

    leaf_names = ["Id", "Timestamp", "Money", "ErrorDetail"]

    # --- Generazione di molte "Entity" che si riferiscono a foglie e fra loro -
    # Ogni Entity produce diversi $ref interni tramite properties, allOf, array.
    # Ogni Entity genera 6 $ref negli schemi + 2 $ref nei paths = 8 in totale.
    refs_per_entity_estimate = 8

    # Regola il numero di entità per avvicinarsi al target richiesto.
    entity_count = max(1, target_refs // refs_per_entity_estimate)

    entity_names = [f"Entity{i:04d}" for i in range(entity_count)]

    for i, name in enumerate(entity_names):
        prev1 = entity_names[i - 1] if i >= 1 else leaf_names[0]
        prev2 = entity_names[i - 2] if i >= 2 else leaf_names[1]

        properties = OrderedDict()

        # $ref verso foglie
        properties["id"] = ref("Id")
        ref_count += 1
        properties["createdAt"] = ref("Timestamp")
        ref_count += 1
        properties["price"] = ref("Money")
        ref_count += 1

        # $ref verso altre entity (grafo di riferimenti)
        properties["parent"] = ref(prev1)
        ref_count += 1

        # array items -> $ref
        properties["children"] = OrderedDict(
            {"type": "array", "items": ref(prev2)}
        )
        ref_count += 1

        schema = OrderedDict({"type": "object", "properties": properties})

        # allOf -> $ref (composizione)
        schema["allOf"] = [ref(prev1)]
        ref_count += 1

        schemas[name] = schema


    # --- Costruzione di paths che referenziano gli schemi --------------------
    paths: "OrderedDict" = OrderedDict()
    for i, name in enumerate(entity_names):
        p = f"/{name.lower()}"
        paths[p] = OrderedDict(
            {
                "get": OrderedDict(
                    {
                        "operationId": f"get{name}",
                        "tags": ["entities"],
                        "responses": OrderedDict(
                            {
                                "200": OrderedDict(
                                    {
                                        "description": "OK",
                                        "content": OrderedDict(
                                            {
                                                "application/json": OrderedDict(
                                                    {"schema": ref(name)}
                                                )
                                            }
                                        ),
                                    }
                                ),
                                "default": OrderedDict(
                                    {
                                        "description": "Errore",
                                        "content": OrderedDict(
                                            {
                                                "application/json": OrderedDict(
                                                    {"schema": ref("ErrorDetail")}
                                                )
                                            }
                                        ),
                                    }
                                ),
                            }
                        ),
                    }
                )
            }
        )
        ref_count += 2

    # --- Wrapper / response schemas per raggiungere esattamente il target ------
    # PageOf<Entity>: ogni wrapper aggiunge 2 $ref. Ne creiamo quanti bastano
    # per colmare la differenza residua verso target_refs.
    wrapper_index = 0
    while ref_count < target_refs and wrapper_index < len(entity_names):
        ent = entity_names[wrapper_index]
        page_name = f"PageOf{ent}"
        schemas[page_name] = OrderedDict(
            {
                "type": "object",
                "properties": OrderedDict(
                    {
                        "content": OrderedDict(
                            {"type": "array", "items": ref(ent)}
                        ),
                        "first": ref(ent),
                        "total": OrderedDict({"type": "integer"}),
                    }
                ),
            }
        )
        ref_count += 2
        wrapper_index += 1

    document = OrderedDict(
        {
            "openapi": "3.0.3",
            "info": OrderedDict(
                {
                    "title": "Internal $ref stress-test API",
                    "version": "1.0.0",
                    "description": (
                        f"Documento generato automaticamente con {ref_count} "
                        "riferimenti interni ($ref)."
                    ),
                }
            ),
            "servers": [
                OrderedDict(
                    {
                        "url": "https://api.example.com/v1",
                        "description": "Server di esempio",
                    }
                )
            ],
            "paths": paths,
            "components": OrderedDict({"schemas": schemas}),
        }
    )

    return document, ref_count


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--out",
        default="openapi-refs.yaml",
        help="Percorso del file YAML di output (default: openapi-refs.yaml)",
    )
    parser.add_argument(
        "--target",
        type=int,
        default=2000,
        help="Numero desiderato di $ref interni (default: 2000)",
    )
    args = parser.parse_args()

    if not (2000 <= args.target <= 5000):
        print(
            f"Attenzione: target={args.target} è fuori dall'intervallo consigliato "
            "2000-5000. Procedo comunque."
        )

    document, ref_count = build_document(args.target)

    with open(args.out, "w", encoding="utf-8") as fh:
        yaml.dump(
            document,
            fh,
            Dumper=_OrderedDumper,
            default_flow_style=False,
            sort_keys=False,
            allow_unicode=True,
        )

    print(f"File scritto: {args.out}")
    print(f"$ref interni generati: {ref_count}")


if __name__ == "__main__":
    main()

