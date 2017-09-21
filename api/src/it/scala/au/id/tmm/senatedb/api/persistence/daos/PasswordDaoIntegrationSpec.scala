package au.id.tmm.senatedb.api.persistence.daos

import au.id.tmm.senatedb.api.integrationtest.PostgresService
import au.id.tmm.utilities.concurrent.FutureUtils.await
import au.id.tmm.utilities.hashing.StringHashing.StringHashingImplicits
import au.id.tmm.utilities.testing.ImprovedFlatSpec
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.BasicAuthProvider

class PasswordDaoIntegrationSpec extends ImprovedFlatSpec with PostgresService {

  private val testLoginInfo = LoginInfo(BasicAuthProvider.ID, "asdf")
  private val testPasswordInfo = {
    val testPassword = "password"
    val testSalt = "asdf"
    val testHash = s"$testPassword$testSalt".sha256checksum.asHexString

    PasswordInfo("sha256", testHash, Some(testSalt))
  }
  private val testPasswordInfo2 = {
    val testPassword = "password123"
    val testSalt = "qwerty"
    val testHash = s"$testPassword$testSalt".sha256checksum.asHexString

    PasswordInfo("sha256", testHash, Some(testSalt))
  }

  private val sut = new PasswordDao(new LoginInfoDao())

  "the password dao" should "return nothing when asked to find a password that wasn't added" in {
    val maybePasswordInfo = await(sut.find(testLoginInfo))

    assert(maybePasswordInfo === None)
  }

  it can "store a password" in {
    val eventualPasswordInfo = for {
      _ <- sut.add(testLoginInfo, testPasswordInfo)
      p <- sut.find(testLoginInfo)
    } yield p

    assert(await(eventualPasswordInfo) === Some(testPasswordInfo))
  }

  it can "update a password" in {
    val eventualPasswordInfo = for {
      _ <- sut.add(testLoginInfo, testPasswordInfo)
      _ <- sut.update(testLoginInfo, testPasswordInfo2)
      p <- sut.find(testLoginInfo)
    } yield p

    assert(await(eventualPasswordInfo) === Some(testPasswordInfo2))
  }

  it should "fail when attempting to update a missing password" in {
    intercept[NoSuchElementException] {
      await(sut.update(testLoginInfo, testPasswordInfo))
    }
  }

  it can "save a new password if it has not already been added" in {
    val eventualPasswordInfo = for {
      _ <- sut.add(testLoginInfo, testPasswordInfo)
      _ <- sut.save(testLoginInfo, testPasswordInfo2)
      p <- sut.find(testLoginInfo)
    } yield p

    assert(await(eventualPasswordInfo) === Some(testPasswordInfo2))
  }

  it can "save a new password by updating existing info" in {
    val eventualPasswordInfo = for {
      _ <- sut.save(testLoginInfo, testPasswordInfo)
      p <- sut.find(testLoginInfo)
    } yield p

    assert(await(eventualPasswordInfo) === Some(testPasswordInfo))
  }

  it can "remove an existing password" in {
    val eventualPasswordInfo = for {
      _ <- sut.add(testLoginInfo, testPasswordInfo)
      _ <- sut.remove(testLoginInfo)
      p <- sut.find(testLoginInfo)
    } yield p

    assert(await(eventualPasswordInfo) === None)
  }
}
