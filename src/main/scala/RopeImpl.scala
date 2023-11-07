package ass2

trait RopeImpl:
  // The following code confines the type of `this` to `this`, so you can do
  // what you want as if you are inside the `Rope` class.
  self: Rope =>

  import Rope.{Leaf, Concat, Slice, Repeat}
  // NOTE: DO NOT MODIFY THE CODE ABOVE THIS LINE.

  def apply(index: Int): Char = if index < 0 then throw new IndexOutOfBoundsException else this match
    case Leaf(text) => text.charAt(index)
    case Concat(left, right) =>
      try {
        left.apply(index)
      } catch {
        case _: StringIndexOutOfBoundsException => right.apply(index - left.length)
      }
    case Slice(rope, start, _) => rope.apply(start + index)
    case Repeat(rope, count) =>
      if (count >= 0) {
        rope.apply(index % rope.length)
      }
      else {
        throw new IndexOutOfBoundsException
      }

  def length: Int = this match
    case Leaf(text) => text.length
    case Concat(left, right) => left.length + right.length
    case Slice(rope, start, end) =>
      val _ = rope.slice(start, end) // check start and end are valid
      if end - start > rope.length then rope.length else end - start
    case Repeat(rope, count) => rope.length * count

  override def toString(): String = this match
    case Leaf(text) => text
    case Concat(left, right) => left.toString() + right.toString()
    case Slice(rope, s1, e1) => if e1 <= s1 then throw new IndexOutOfBoundsException else rope match
      case Leaf(text) => text.slice(s1, e1)
      case Repeat(r2, count) =>
        val upperBound = e1 / r2.length + 1
        val lowerBound = s1 / r2.length
        if upperBound > count
        then throw new IndexOutOfBoundsException
        else (r2.toString() * (upperBound - lowerBound)).slice(s1 % r2.length, s1 % r2.length + e1 - s1)
      case slice2@Slice(_, s2, e2) =>
        if s2 >= s1 && e2 <= e1
        then slice2.toString()
        else throw new IndexOutOfBoundsException
      case Concat(left, right) =>
        val leftLength = left.length
        if (e1 < leftLength) {
          left.slice(s1, e1).toString()
        }
        else if (s1 >= leftLength) {
          right.slice(s1, e1).toString()
        }
        else {
          // slice is in the middle
          val leftSlice = left.slice(s1, leftLength)
          val rightSlice = right.slice(0, e1 - leftLength)

          (leftSlice + rightSlice).toString()
        }
    case Repeat(rope, count) => rope.toString() * count

  def toStringBad(): String = this match
    case Leaf(text) => text
    case Concat(left, right) => left.toStringBad() + right.toStringBad()
    case Slice(rope, s1, e1) => rope.toStringBad().slice(s1, e1)
    case Repeat(rope, count) => rope.toStringBad() * count

  def indexOf(text: String, start: Int): Int = this match
    case Leaf(t) => t.indexOf(text, start)
    case Concat(left, right) =>
      if (start > left.length - 1) {
        val rightRes = right.indexOf(text, start - left.length)
        if rightRes == -1 then rightRes else rightRes + left.length
      }
      else {
        val leftRes = left.indexOf(text, start)
        if (leftRes != -1) {
          leftRes
        }
        else {
          // can find from middle?
          var breakLoop = false
          var res: Int = -1;

          for (i <- 1 to text.length if !breakLoop) {
            if (left.indexOf(text.take(i), start) != -1 && right.indexOf(text.drop(i), 0) != -1) {
              breakLoop = true
              res = left.indexOf(text.take(i), start)
            }
          }

          res
        }
      }
    case s@Slice(_, _, _) =>
      s.toString().indexOf(text)
    case r@Repeat(rope, count) =>
      val lowerBound = start / rope.length
      val span = text.length / rope.length + 2
      if lowerBound > count
      then throw new IndexOutOfBoundsException
      else
        val offset = (rope.toString() * span).indexOf(text, start % rope.length)
        if offset == -1
        then -1
        else
          val res = (rope.toString() * span).indexOf(text, start % rope.length) + start - start % rope.length
          if res + text.length >= r.length // out of bound
          then -1
          else res

  def insert(rope: Rope, at: Int): Rope = ???

  def delete(start: Int, end: Int): Rope = ???

  def split(separator: String): List[Rope] = ???

  def replace(text: String, replacement: String): Rope = ???

  def duplicate(start: Int, end: Int, times: Int): Rope = ???

  def simplify: Rope = ???
