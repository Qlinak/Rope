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

  private lazy val cachedLength: Int = calculateLength

  private def calculateLength: Int = this match
    case Leaf(text) => text.length
    case Concat(left, right) => left.length + right.length
    case Slice(rope, start, end) =>
      val _ = rope.slice(start, end) // check start and end are valid
      if end - start > rope.length then rope.length else end - start
    case Repeat(rope, count) => rope.length * count

  def length: Int = cachedLength

  private lazy val cachedToString: String = privateToString

  private def privateToString: String = this match
    case Leaf(text) => text
    case Concat(left, right) => left.toString() + right.toString()
    case Slice(rope, s1, e1) => if e1 < s1 then throw new IndexOutOfBoundsException else if e1 == s1 then "" else rope match
      case Leaf(text) => text.slice(s1, e1)
      case Repeat(r2, count) =>
        if r2.length == 0 then ""
        else
          val upperBound = e1 / r2.length + 1
          val lowerBound = s1 / r2.length
          if upperBound - 1 > count
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
          right.slice(s1 - leftLength, e1 - leftLength).toString()
        }
        else {
          // slice is in the middle
          val leftSlice = left.slice(s1, leftLength)
          val rightSlice = right.slice(0, e1 - leftLength)

          (leftSlice + rightSlice).toString()
        }
    case Repeat(rope, count) => rope.toString() * count

  override def toString(): String = cachedToString

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
          var res: Int = -1

          for (i <- 1 to text.length if !breakLoop) {
            val leftSplit = left.indexOf(text.take(i), start)
            val rightSplit = right.indexOf(text.drop(i), 0)
            if (leftSplit != -1 && rightSplit != -1) {
              breakLoop = true
              res = leftSplit
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
          val res = offset + start - start % rope.length
          if res + text.length >= r.length // out of bound
          then -1
          else res

  extension (s: String)
    def insert(text: String, dest: Int): String = s.substring(0, dest) + text + s.substring(dest)

    def delete(start: Int, end: Int): String = s.substring(0, start) + s.substring(end)
  def insert(rope: Rope, at: Int): Rope =
    if at < 0 && at > rope.length
    then throw new IndexOutOfBoundsException
    else Leaf(this.toString().insert(rope.toString(), at))

  def delete(start: Int, end: Int): Rope =
    if start < 0 || end > length || start > end
    then throw new IndexOutOfBoundsException()
    else Leaf(this.toString().delete(start, end))

  def split(separator: String): List[Rope] =
    if (separator.isEmpty) {
      this.toString().split(separator).map(x => Leaf(x)).toList
    }
    else if(this.toString() == separator){
      List(Leaf(""), Leaf(""))
    }
    else
      val start = this.indexOf(separator, 0)
      if (start == -1) {
        List(this)
      }
      else if(start == 0){
        Leaf("") :: this.delete(0, 0 + separator.length).split(separator)
      }
      else {
        Leaf(this.toString().substring(0, start)) :: this.delete(0, start + separator.length).split(separator)
      }

  def replace(text: String, replacement: String): Rope = Leaf(this.toString().replaceAll(text, replacement))

  def duplicate(start: Int, end: Int, times: Int): Rope =
    if times < 0
    then throw new IllegalArgumentException
    else if times == 0
    then this.delete(start, end)
    else this.slice(0, start) + this.slice(start, end) * times + this.slice(end, length)

  private def splitAt(index: Int): (Rope, Rope) = (this.slice(0, index), this.slice(index + 1, this.length))

  def simplify: Rope = this match
    case Leaf(_) => this
    case Concat(left, right) =>
      if(left.length > 0 && right.length > 0){
        this
      }
      else if(left.length == 0){
        right.simplify
      }
      else {
        left.simplify
      }
    case Repeat(r1, c1) => c1 match
      case 0 => Rope.empty
      case 1 => r1.simplify
      case _ =>
        val r1Res = r1.simplify
        if r1Res == Rope.empty
        then Rope.empty
        else (1 until c1).foldLeft(r1Res)((acc, _) => acc + r1Res)
    case Slice(r1, s1, e1) => r1 match
      case Leaf(txt) => Leaf(txt.slice(s1, e1))
      case Concat(left, right) =>
        if(e1 <= left.length){
          Slice(left, s1, e1).simplify
        }
        else if(s1 >= left.length){
          Slice(right, s1-left.length, e1-left.length).simplify
        }
        else{
          // in middle
          Slice(left, s1, left.length).simplify + Slice(right, 0, e1-left.length).simplify
        }
      case Slice(r2, s2, e2) =>
        if s2 >= s1 && e2 <= e1
        then Slice(r2, s2, e2).simplify
        else throw new IndexOutOfBoundsException
      case Repeat(r2, c1) => Repeat(r2, c1).simplify.slice(s1, e1).simplify
