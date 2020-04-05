package gov.mm.covid19statsbago.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import gov.mm.covid19statsbago.R
import gov.mm.covid19statsbago.activities.BottomNavigationActivity
import gov.mm.covid19statsbago.adapter.TableAdapter
import gov.mm.covid19statsbago.datas.CovidCountry
import gov.mm.covid19statsbago.datas.columnHeaderList
import gov.mm.covid19statsbago.datas.rowHeaderList
import gov.mm.covid19statsbago.datas.tableCellList
import gov.mm.covid19statsbago.generals.toMMDate
import gov.mm.covid19statsbago.generals.toUniNumber
import gov.mm.covid19statsbago.jsonparsings.JsonParsingDashboardList
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val tableAdapter: TableAdapter by lazy {
        TableAdapter(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        table_view.apply {
            setHasFixedWidth(false)
            adapter = tableAdapter
        }

        swipe_refresh.apply {
            isRefreshing = true
            setOnRefreshListener {
                refreshData()
            }
        }

    }

    private fun tableDataBind(countryList: List<CovidCountry>) {
        tableAdapter.setAllItems(
            columnHeaderList {
                (1..4).forEach {
                    columnHeader {
                        data = when (it) {
                            1 -> "နိုင်ငံများ "
                            2 -> "ပိုးတွေ့ရှိသူ"
                            3 -> "သေဆုံးသူ"
                            else -> "ပြန်ကောင်းသူ"
                        }
                    }
                }
            },
            rowHeaderList {
                countryList.filter { it.country.isNotEmpty() }.mapIndexed { index, _ ->
                    rowHeader {
                        data = "${index + 1}"
                    }
                }
            },
            countryList.filter { it.country.isNotEmpty() }.mapIndexed { index, covidCountry ->
                tableCellList {
                    tableCell {
                        cellId = index.toString()
                        data = covidCountry.country
                    }
                    tableCell {
                        cellId = index.toString()
                        data = covidCountry.totalConfirmed.toUniNumber()
                    }
                    tableCell {
                        cellId = index.toString()
                        data = covidCountry.totalDeaths.toUniNumber()
                    }
                    tableCell {
                        cellId = index.toString()
                        data = covidCountry.totalRecovered.toUniNumber()
                    }
                }
            }
        )
    }

    private fun refreshData() {
        shimmerlayout.visibility = View.VISIBLE
        shimmerlayout.startShimmerAnimation()
        allcountrylist.visibility = View.GONE
        JsonParsingDashboardList().getResponseForDashboard(
            success = { data, date ->
                if (activity == null || (activity as BottomNavigationActivity).currentFragment != 0) return@getResponseForDashboard
                swipe_refresh.apply {
                    isRefreshing = false
                    isEnabled = false
                }
                tv_global_confirm_count.text = data.sumBy { it.totalConfirmed }.toUniNumber()
                tv_global_death_count.text = data.sumBy { it.totalDeaths }.toUniNumber()
                tv_global_recover_count.text = data.sumBy { it.totalRecovered }.toUniNumber()

                tv_today_date?.text = date.toMMDate()

                if (data.any { it.country == "Myanmar" }) {
                    with(data.first { it.country == "Myanmar" }) {
                        tv_mm_confirm_count.text = totalConfirmed.toUniNumber()
                        tv_mm_death_count.text = totalDeaths.toUniNumber()
                        tv_mm_recover_count.text = totalRecovered.toUniNumber()
                    }
                }

                countrylistcardview.visibility = View.VISIBLE
                shimmerlayout.visibility = View.GONE
                shimmerlayout.stopShimmerAnimation()
                allcountrylist.visibility = View.VISIBLE
                tableDataBind(data)

            },
            error = {
                if (activity == null || (activity as BottomNavigationActivity).currentFragment != 0) return@getResponseForDashboard
                swipe_refresh.apply {
                    isRefreshing = false
                }
            }
        )
    }

    override fun onResume() {
        (activity as BottomNavigationActivity).apply {
            currentFragment = 0
        }
        refreshData()
        super.onResume()
    }
}
