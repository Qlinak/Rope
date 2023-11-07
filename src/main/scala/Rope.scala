package ass2

enum Rope extends RopeInterface[Rope], RopeImpl:
  // Note: `private[ass2]` means that the case class is only visible to code
  // inside the ass2 package. So, we can use them in test suites.
  private[ass2] case Leaf(text: String)
  private[ass2] case Concat(left: Rope, right: Rope)
  private[ass2] case Slice(rope: Rope, start: Int, end: Int)
  private[ass2] case Repeat(rope: Rope, count: Int)

  def +(that: Rope): Rope = Concat(this, that)
  def *(times: Int): Rope =
    if times < 0
    then throw new IllegalArgumentException
    else Repeat(this, times)
  def slice(start: Int, end: Int): Rope =
    if 0 <= start && start <= end && end <= length
    then Slice(this, start, end)
    else throw new IndexOutOfBoundsException

object Rope:
  def apply(text: String): Rope = Leaf(text)
  def empty: Rope = Leaf("")
