package de.htwg.swe.evenup.util

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ObservableSpec extends AnyWordSpec with Matchers:

  "Observable" should {

    "notify its observers with default ObservableEvent" in:
      // test observer
      var receivedEvent: Option[ObservableEvent] = None
      val testObserver                           =
        new Observer:
          override def update(e: ObservableEvent): Unit = receivedEvent = Some(e)

      val observable = new Observable()
      observable.add(testObserver)

      // call notifyObservers without argument
      observable.notifyObservers()

      receivedEvent should not be None
      receivedEvent.get shouldBe a[ObservableEvent]

    "notify observers with a custom event" in:
      var receivedEvent: Option[ObservableEvent] = None
      val testObserver                           =
        new Observer:
          override def update(e: ObservableEvent): Unit = receivedEvent = Some(e)

      val observable = new Observable()
      observable.add(testObserver)

      val customEvent = new ObservableEvent
      observable.notifyObservers(customEvent)

      receivedEvent shouldBe Some(customEvent)

    "remove observers correctly" in:
      var called = false
      val obs    =
        new Observer:
          override def update(e: ObservableEvent): Unit = called = true

      val observable = new Observable()
      observable.add(obs)
      observable.remove(obs)

      observable.notifyObservers()
      called shouldBe false
  }
