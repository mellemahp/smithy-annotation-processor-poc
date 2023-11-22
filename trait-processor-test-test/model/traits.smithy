$version: "2.0"

namespace io.smithy.example

@myComplexTrait(
    fieldA: "A"
    fieldB: false
    fieldC: {fieldN: "a", fieldQ: false, fieldZ: "B"}
    fieldD: ["a", "b", "c"]
    fieldE: {a: "b", c: "d"}
)
string MyStr

structure MyStruct {
    @myIntEnum(1)
    @myEnum("yes")
    @jsonName("Coolio")
    fieldA: String
}

@strList(["a", "b", "ccc", "d"])
integer MyInt
