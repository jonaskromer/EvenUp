package de.htwg.swe.evenup.util

import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ObservableSpec extends AnyWordSpec with Matchers:

  "Observable" should {

    "start with no subscribers" in:
      val observable = new Observable
      observable.subscribers shouldBe empty

    "add a subscriber" in:
      val observable = new Observable
      val observer = new Observer:
        var updated = false
        def update(e: ObservableEvent): Unit = updated = true

      observable.add(observer)
      observable.subscribers should contain(observer)
      observable.subscribers.length shouldBe 1

    "add multiple subscribers" in:
      val observable = new Observable
      val observer1 = new Observer:
        def update(e: ObservableEvent): Unit = ()
      val observer2 = new Observer:
        def update(e: ObservableEvent): Unit = ()

      observable.add(observer1)
      observable.add(observer2)
      observable.subscribers.length shouldBe 2

    "remove a subscriber" in:
      val observable = new Observable
      val observer = new Observer:
        def update(e: ObservableEvent): Unit = ()

      observable.add(observer)
      observable.subscribers.length shouldBe 1
      observable.remove(observer)
      observable.subscribers shouldBe empty

    "notify all subscribers" in:
      val observable = new Observable
      var count = 0
      val observer1 = new Observer:
        def update(e: ObservableEvent): Unit = count += 1
      val observer2 = new Observer:
        def update(e: ObservableEvent): Unit = count += 1

      observable.add(observer1)
      observable.add(observer2)
      observable.notifyObservers()
      count shouldBe 2

    "notify subscribers with event" in:
      val observable = new Observable
      var receivedEvent: Option[ObservableEvent] = None
      val event = new ObservableEvent
      val observer = new Observer:
        def update(e: ObservableEvent): Unit = receivedEvent = Some(e)

      observable.add(observer)
      observable.notifyObservers(event)
      receivedEvent shouldBe Some(event)

    "not fail when removing non-existent subscriber" in:
      val observable = new Observable
      val observer = new Observer:
        def update(e: ObservableEvent): Unit = ()

      observable.remove(observer)
      observable.subscribers shouldBe empty
  }

  "Observer" should {

    "be a trait with update method" in:
      val observer = new Observer:
        var lastEvent: Option[ObservableEvent] = None
        def update(e: ObservableEvent): Unit = lastEvent = Some(e)

      observer shouldBe a[Observer]
  }

  "ObservableEvent" should {

    "be instantiable" in:
      val event = new ObservableEvent
      event shouldBe a[ObservableEvent]
  }
