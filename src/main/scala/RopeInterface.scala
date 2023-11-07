package ass2

trait RopeInterface[Rope]:
  def apply(index: Int): Char

  def length: Int

  override def toString(): String

  def indexOf(text: String, start: Int): Int

  def insert(rope: Rope, after: Int): Rope

  def delete(start: Int, end: Int): Rope

  def split(separator: String): List[Rope]

  def replace(text: String, replacement: String): Rope

  def duplicate(start: Int, end: Int, times: Int): Rope

  def simplify: Rope
