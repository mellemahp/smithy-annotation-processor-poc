$version: "2.0"

namespace test.smithy.traitcodegen

/// A basic annotation trait
@trait(selector: "structure > :test(member > string)")
structure basicAnnotationTrait {}

/// Simple String trait
@trait(selector: "member")
string stringTrait

// ===============
//  Number traits
// ===============
@trait
integer HttpCodeInteger

@trait
long HttpCodeLong

@trait
short HttpCodeShort

@trait
float HttpCodeFloat

@trait
double HttpCodeDouble

// ===========
// List traits
// ===========
/// A list with only a simple string member
@trait
list StringListTrait {
    member: String
}

@trait
list NumberListTrait {
    member: Integer
}

@trait
list StructureListTrait {
    member: listMember
}

@private
structure listMember {
    a: String
    b: Integer
    c: String
}

// ===========
// Map traits
// ===========
/// Map of only simple strings. These are handled slightly differently than
/// other maps
@trait
map StringStringMap {
    key: String
    value: String
}

@trait
map StringToStructMap {
    key: String
    value: MapValue
}

@private
structure MapValue {
    a: String
    b: Integer
}
