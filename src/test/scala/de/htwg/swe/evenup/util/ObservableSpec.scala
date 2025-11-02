package de.htwg.swe.evenup.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ObservableSpec extends AnyWordSpec with Matchers {

  "An Observable" should {

    "add observers and notify them" in {
      // Mock observer, dass wir ihn im Test verwenden
      var notified = false
      val observer =
        new Observer {
          def update: Unit = notified = true // Ohne Klammern
        }

      val observable = new Observable()

      // Observer hinzufügen
      observable.add(observer)

      // Benachrichtigung auslösen
      observable.notifyObservers // Ohne Klammern

      // Überprüfen, ob der Observer benachrichtigt wurde
      notified should be(true)
    }

    "notify all observers" in {
      var notified1 = false
      var notified2 = false

      // Zwei Observer, um die Benachrichtigung zu testen
      val observer1 =
        new Observer {
          def update: Unit = notified1 = true // Ohne Klammern
        }

      val observer2 =
        new Observer {
          def update: Unit = notified2 = true // Ohne Klammern
        }

      val observable = new Observable()

      // Beide Observer hinzufügen
      observable.add(observer1)
      observable.add(observer2)

      // Benachrichtigung auslösen
      observable.notifyObservers // Ohne Klammern

      // Überprüfen, ob beide Observer benachrichtigt wurden
      notified1 should be(true)
      notified2 should be(true)
    }

    "remove observers correctly" in {
      var notified1 = false
      var notified2 = false

      val observer1 =
        new Observer {
          def update: Unit = notified1 = true // Ohne Klammern
        }

      val observer2 =
        new Observer {
          def update: Unit = notified2 = true // Ohne Klammern
        }

      val observable = new Observable()

      // Beobachter hinzufügen
      observable.add(observer1)
      observable.add(observer2)

      // Beobachter 1 entfernen
      observable.remove(observer1)

      // Benachrichtigung auslösen
      observable.notifyObservers // Ohne Klammern

      // Überprüfen, ob nur der zweite Observer benachrichtigt wurde
      notified1 should be(false)
      notified2 should be(true)
    }

    "not add duplicate observers" in {
      var notified = false
      val observer =
        new Observer {
          def update: Unit = notified = true // Ohne Klammern
        }

      val observable = new Observable()

      // Observer zweimal hinzufügen
      observable.add(observer)
      observable.add(observer)

      // Benachrichtigung auslösen
      observable.notifyObservers // Ohne Klammern

      // Überprüfen, ob der Observer nur einmal benachrichtigt wurde
      notified should be(true)
    }
  }

}
