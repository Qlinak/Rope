# Introduction

A string is one of the commonly used data structures. It uses an array for storage internally. Consequently, both deleting the text from the middle of the string and inserting text into the string have a linear time complexity.

However, this method of storage is inefficient both in terms of time and space for applications that require frequent manipulation of large amounts of text, such as text editors. To solve this problem, the rope data structure was introduced. It is often implemented as a tree structure, with plain strings as the leaves of the tree, and various common operations on strings as the internal nodes.

So, we deal with the following `Rope` definitions.

```scala
enum Rope:
  case Leaf(text: String)
  case Concat(left: Rope, right: Rope)
  case Slice(rope: Rope, start: Int, end: Int)
  case Repeat(rope: Rope, count: Int)
```

`Leaf` refers to the leaf node and contains a plain string. `Concat` signifies the linking of two Ropes. `Slice` refers to stripping a `Rope` from the start to end characters. `Repeat` repeats a particular `Rope` `count` times.

The following methods are available in rope manipulation

## Access

### Indexing

```scala
def apply(index: Int): Char = ???
```

This method returns the `index`-th character from this `Rope`. If `index` is less than zero or greater than `length`. Your implementation should throw `IndexOutOfBoundsException` (note that `StringIndexOutOfBoundsException` also works).

### Length

```scala
def length: Int = ???
```

This method returns the number of characters in this `Rope`. Hint: to enhance the efficiency, you can calculate and store `length` in advance, or change `def` to `lazy val`.

### Convert to Strings

```scala
override def toString(): String = ???
```

This method converts this `Rope` to a built-in `String`.

## Search

```scala
def indexOf(text: String, start: Int): Int = ???
```

This method locates the index within this `Rope` of the first occurrence of the given substring `text`, starting at the given index. If the rope does not contain such text, returns -1 instead. Do not check `start` is valid or not.

## Manipulation

### Insertion

```scala
def insert(rope: Rope, at: Int): Rope = ???
```

This method inserts the content of another `Rope` right after the given index and returns a new rope. The valid range of parameter `at` is `[0, length]`. Refer to the `apply` function to determine what exception should be thrown for illegal indices.

### Deletion

```scala
def delete(start: Int, end: Int): Rope = ???
```

This method remove substring from `start` (inclusive) to `end` (exclusive) and returns a new rope. The valid range of both parameters are `[0, length]` and `start` should be less than or equal to `end`. Refer to the `apply` function to determine what exception should be thrown for illegal indices.

### Splitting

```scala
def split(separator: String): List[Rope] = ???
```

This method splits the rope into several smaller `Rope`s around the separator string. If `separator` is empty, the behavior of this function should resemble that of a String.

### Replacement

```scala
def replace(text: String, replacement: String): Rope = ???
```

This method replaces each substring of this `Rope` that matches the given string with the given replacement string. Note that two special cases need to be considered. If the `text` is empty, `replace` function should insert `replacement` string at every position. And we do not take into account the repeated replacement caused by the case that `text` contains `replacement`.

### Sub-string Duplication

```scala
def duplicate(start: Int, end: Int, times: Int): Rope = ???
```

This function repeats substring within given range represented by `start` and `end` for given times. Note that `times` can be zero but cannot be negative. If `times` is zero, the method should remove the substring from this `Rope`. The valid range of both parameters are `[0, length]` and `start` should be less than or equal to `end`. Refer to the `apply` function to determine what exception should be thrown for illegal indices. Moreoever, you should ensure that it is not negative. Otherwise, throw a `IllegalArgumentException`

# Optimization

```scala
def simplify: Rope = ???
```

For all Rope `s`, assume that we call `s.simplify` and get a new `Rope` `t`.

1. If `t.length` is 0, it must be `Leaf("")`. Otherwise, the `length` of *any* child node of `t` cannot be 0.
2. All descendent nodes of `t` should be either `Leaf` or `Concat`, *not* `Slice` or `Repeat`.
3. For all `s`’ descendent `Concat` node `u`, let us denote `u`’s left and right child nodes bu `l` and `r`. If *both* `l.length` and `r.length` are not `0`, then `u.simplify` should also appear in `t`. In other words, `Concat` nodes where none of the child nodes are empty will **NOT** be reduced.
