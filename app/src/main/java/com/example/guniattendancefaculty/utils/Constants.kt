package com.example.guniattendancefaculty.utils

object Constants {

    const val DST_WIDTH = 400
    const val DST_HEIGHT = 400

    private const val DEPLOYMENT_ID =
        "AKfycbxCtot6TXWZovqEMdDOyXyw6pgnsLw_VYv_x_I8owrcb8sZPa8C0vNIsyKV_Nm5_R4qIQ"
    const val GOOGLE_SPREADSHEET_LINK =
        "https://script.google.com/macros/s/$DEPLOYMENT_ID/exec"
    const val SHEET_ACTION = "addAttendance"

    val AI_SUBJECT_NAMES = arrayListOf(
        "Machine Learning",
        "Computational Data Analytics",
        "ML Ops"
    )

    val test: ArrayList<Pair<ArrayList<String>, ArrayList<String>>> = arrayListOf(
        Pair(arrayListOf(), arrayListOf()),
        Pair(arrayListOf(), arrayListOf()),
        Pair(arrayListOf(), arrayListOf()),
        Pair(arrayListOf(), arrayListOf()),
        Pair(
            arrayListOf(
                "Machine Learning",
                "Computational Data Analytics"
            ), arrayListOf("")
        ),
        Pair(arrayListOf(), arrayListOf()),
        Pair(arrayListOf("ML Ops"), arrayListOf("AI")),
        Pair(arrayListOf(), arrayListOf())
    )

    val DATA: ArrayList<Triple<ArrayList<String>, ArrayList<String>, ArrayList<String>>> =
        arrayListOf(
            Triple(
                arrayListOf("abc", "xyz"),
                arrayListOf("CEITA", "CEITB", "CEITC", "CEITD"),
                arrayListOf("AB1", "AB2", "AB3", "AB4", "AB5", "AB6", "AB7", "AB8")
            ),
            Triple(
                arrayListOf("abc", "xyz"),
                arrayListOf("2CEITA", "2CEITB", "2CEITC", "2CEITD"),
                arrayListOf("2AB1", "2AB2", "2AB3", "2AB4", "2AB5", "2AB6", "2AB7", "2AB8")
            ),
            Triple(
                arrayListOf("abc", "xyz"),
                arrayListOf("3CEITA", "3CEITB", "3CEITC", "3CEITD"),
                arrayListOf("3AB1", "3AB2", "3AB3", "3AB4", "3AB5", "3AB6", "3AB7", "3AB8")
            ),
            Triple(
                arrayListOf("abc", "xyz"),
                arrayListOf("4CEITA", "4CEITB", "4CEITC", "4CEITD"),
                arrayListOf("4AB1", "4AB2", "4AB4", "4AB4", "4AB5", "4AB6", "4AB7", "4AB8")
            ),
            Triple(
                arrayListOf(
                    "Computer Architecture & Organization",
                    "Software Engineering",
                    "Computer Networks",
                    "Aptitude Skill Building-I",
//                    "Computer Graphics & Visualization",
//                    "Software Packages",
                    "Mobile Application Development",
//                    "Innovation & Entrepreneurship",
//                    "Machine Learning",
//                    "Computational Data Analytics"
                ),
                arrayListOf("CEIT-A", "CEIT-B", "CEIT-C", "CEIT-D"),
                arrayListOf(
                    "AB1",
                    "AB2",
                    "AB3",
                    "AB4",
                    "AB5",
                    "AB6",
                    "AB7",
                    "AB8",
                    "AB9",
                    "AB10",
                    "AB11",
                    "AB12",
                    "AB13",
                    "AB14",
                    "AB15",
                    "AB16",
                    "AI1",
                    "AI2"
                )
            ),
            Triple(
                arrayListOf("abc", "xyz"),
                arrayListOf("6CEITA", "6CEITB", "6CEITC", "6CEITD"),
                arrayListOf("6AB1", "6AB2", "6AB6", "6AB6", "6AB6", "6AB6", "6AB7", "6AB8")
            ),
            Triple(
                arrayListOf(
                    "Big Data Analytics",
                    "Compiler Design",
                    "Forensics & Cyber Law",
                    "ML Ops"
                ),
                arrayListOf("CEIT-A", "CEIT-B", "CEIT-C"),
                arrayListOf(
                    "AB1",
                    "AB2",
                    "AB3",
                    "AB4",
                    "AB5",
                    "AB6",
                    "AB7",
                    "AB8",
                    "AB9",
                    "AB10",
                    "AB11",
                    "AB12",
                    "AI"
                )
            ),
            Triple(
                arrayListOf("abc", "xyz"),
                arrayListOf("8CEITA", "8CEITB", "8CEITC", "8CEITD"),
                arrayListOf("8AB1", "8AB2", "8AB8", "8AB8", "8AB8", "8AB8", "8AB8", "8AB8")
            )
        )

    val SHEET_URLS_LECTURE: ArrayList<Map<String, ArrayList<String>>> = arrayListOf(
        mapOf(),
        mapOf(),
        mapOf(),
        mapOf(),
        mapOf(
            "Computer Architecture & Organization" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=1373341867",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=354436464",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=1934786778",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=426808070"
            ),
            "Software Engineering" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=1373341867",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=354436464",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=1934786778",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=426808070"
            ),
            "Computer Networks" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=1373341867",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=354436464",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=1934786778",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=426808070"
            ),
            "Aptitude Skill Building-I" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=1373341867",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=354436464",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=1934786778",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=426808070"
            ),
            "Mobile Application Development" to arrayListOf(
                "",
                "",
                "https://docs.google.com/spreadsheets/d/1S395iPf7HMto-XEewhh9H5EwBpLq1Z2lza9ZMCv-nOQ/edit#gid=1934786778",
                "https://docs.google.com/spreadsheets/d/1S395iPf7HMto-XEewhh9H5EwBpLq1Z2lza9ZMCv-nOQ/edit#gid=426808070"
            )
        ),
        mapOf(),
        mapOf(
            "Big Data Analytics" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=1373341867",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=1667378169",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=1934786778"
            ),
            "Compiler Design" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=1373341867",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=1667378169",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=1934786778"
            ),
            "Forensics & Cyber Law" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=1373341867",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=1667378169",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=1934786778"
            ),
            "ML Ops" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/12iqV7YEEf6hnF8cUz4q1ytjikRe8hpe0Rds3a0E6rGA/edit#gid=1667378169"
            )
        ),
        mapOf()
    )


    val SHEET_URLS_LABS: ArrayList<Map<String, ArrayList<String>>> = arrayListOf(
        mapOf(),
        mapOf(),
        mapOf(),
        mapOf(),
        mapOf(
            "Computer Architecture & Organization" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=597031345",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=1518726815",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=1860904362",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=1436365236",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=127769415",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=385150192",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=737939752",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=1074142734",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=181717103",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=1398776127",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=927842467",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=1519799318",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=1161647623",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=238244867",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=2140177559",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=633528821",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=1395617786",
                "https://docs.google.com/spreadsheets/d/1VmNd3HwpOAIqnrZEWnNBDLOdceQl-JglGNToKLr5-lo/edit#gid=662959215"
            ),
            "Software Engineering" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=597031345",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=1518726815",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=1860904362",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=1436365236",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=127769415",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=385150192",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=737939752",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=1074142734",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=181717103",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=1398776127",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=927842467",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=1519799318",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=1161647623",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=238244867",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=2140177559",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=633528821",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=1395617786",
                "https://docs.google.com/spreadsheets/d/1P7LBqphRsz9qvUMPkALORLXGv84T9zZJ1u4YO76yvIw/edit#gid=662959215"
            ),
            "Computer Networks" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=597031345",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=1518726815",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=1860904362",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=1436365236",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=127769415",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=385150192",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=737939752",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=1074142734",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=181717103",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=1398776127",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=927842467",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=1519799318",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=1161647623",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=238244867",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=2140177559",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=633528821",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=1395617786",
                "https://docs.google.com/spreadsheets/d/19X79xcdJMjbOkIgltj_Ph3uKi647j-uisxbeNERNPNg/edit#gid=662959215"
            ),
            "Aptitude Skill Building-I" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=597031345",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=1518726815",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=1860904362",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=1436365236",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=127769415",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=385150192",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=737939752",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=1074142734",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=181717103",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=1398776127",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=927842467",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=1519799318",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=1161647623",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=238244867",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=2140177559",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=633528821",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=1395617786",
                "https://docs.google.com/spreadsheets/d/1K8Ypx7DQoLn8HXCaY-cuHIeT4ZV_BKVS2xwf8MQ6yNA/edit#gid=662959215"
            )
        ),
        mapOf(),
        mapOf(
            "Big Data Analytics" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=597031345",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=1518726815",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=1860904362",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=1436365236",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=127769415",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=385150192",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=737939752",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=1074142734",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=181717103",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=1398776127",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=927842467",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=1085028139",
                "https://docs.google.com/spreadsheets/d/1XfTTorSMal8g4xR8pTNSNU3fPrB7axw-EtsjArCUccc/edit#gid=1395617786"
            ),
            "Compiler Design" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=597031345",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=1518726815",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=1860904362",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=1436365236",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=127769415",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=385150192",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=737939752",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=1074142734",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=181717103",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=1398776127",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=927842467",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=1085028139",
                "https://docs.google.com/spreadsheets/d/1FVVu5Mj3_mlYnm5WJTuH_FwufQ1WpfS0Qs5s__p19Ss/edit#gid=1395617786"
            ),
            "Forensics & Cyber Law" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=597031345",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=1518726815",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=1860904362",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=1436365236",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=127769415",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=385150192",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=737939752",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=1074142734",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=181717103",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=1398776127",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=927842467",
                "https://docs.google.com/spreadsheets/d/1xuOxb8gSfuMLx1u7WTo2V4g1cNJYwopZl1DkYyGdZAE/edit#gid=1085028139"
            ),
            "ML Ops" to arrayListOf(
                "https://docs.google.com/spreadsheets/d/12iqV7YEEf6hnF8cUz4q1ytjikRe8hpe0Rds3a0E6rGA/edit#gid=1395617786"
            )
        ),
        mapOf()
    )
}