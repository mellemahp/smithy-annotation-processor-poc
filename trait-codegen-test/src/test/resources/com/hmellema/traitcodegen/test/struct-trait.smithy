$version: "2.0"

namespace test.smithy.traitcodegen

@structureTrait(
    fieldA: "first"
    fieldB: false
    fieldC: {
        fieldN: "nested"
        fieldQ: true
        fieldZ: "A"
    }
    fieldD: ["a", "b", "c"]
    fieldE: {
        a: "one"
        b: "two"
    }
)
structure myStruct {
}
