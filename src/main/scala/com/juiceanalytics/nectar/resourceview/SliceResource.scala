package com.juiceanalytics.nectar.resourceview

import com.google.inject.Inject
import com.juiceanalytics.nectar.security.AuthenticatedContext
import javax.ws.rs.{GET, Path, Produces}
import com.juiceanalytics.nectar.model.Slice
import java.util.List
import java.util.ArrayList


/**
 * Is the {@link User} model entity resource view.
 *  
 * @author Glenn renfro
 */
@Path("slice")
trait SliceResource {

  @GET
  @Path("current")
  @Produces(Array("application/vnd.juiceanalytics+json", "application/json"))
  def current: SliceBean

  @GET
  @Path("list")
  @Produces(Array("application/vnd.juiceanalytics+json", "application/json"))
  def sliceList:SliceList
}

/**
 * Is the default implementation of a {@link SliceResource}.
 */
class SliceResourceImpl @Inject() (val slice:Slice) extends SliceResource {
  def current: SliceBean = new SliceBean(slice)
  def sliceList :SliceList    = new SliceList(Slice.getAll)

}


case class SliceList(sliceVal: List[Slice]){
  val tmpList = new ArrayList[SliceBean]
  val iter =  sliceVal.iterator()
  while(iter.hasNext) {
    val slice = iter.next
    tmpList.add(new SliceBean(slice))
  }
  def getSlices:List[SliceBean] = new ArrayList[SliceBean](tmpList)
}

/**
 * Exposes the {@link User} as a Java bean to the Jackson encoder.
 */
case class SliceBean(slice: Slice) {

  def getId = slice.id
  def getType = slice.sliceType
  def getTitle = slice.title
  def getSubTitle = slice.subtitle
  def getIndex = slice.index;
}
