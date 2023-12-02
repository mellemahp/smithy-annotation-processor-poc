$version: "2.0"

namespace test.smithy.traitcodegen

/// A basic annotation trait
@trait(selector: "structure > :test(member > string)")
structure basicAnnotationTrait {}

/// Simple String trait
@trait(selector: "member")
string stringTrait
