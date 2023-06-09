import com.benjtissot.sellingmugs.entities.printify.ImageForUpload
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.entities.printify.MugProductInfo
import com.benjtissot.sellingmugs.services.PrintifyService
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PrintifyTests : AbstractDatabaseTests(){

    private val LOG = KtorSimpleLogger("PrintifyTests.kt")

    @Test
    fun managingProduct() = runTest {
        launch(Dispatchers.Main) {
            // Upload Image
            val imageForUploadReceive = uploadImageTest()

            // Create Product
            val productId = createProductTest(imageForUploadReceive)

            // Publish Product if productId is not null
            publishProductTest(productId)

            // Delete the published product if it has been published
            val deletedCode = PrintifyService.deleteProduct(productId)
            assert(deletedCode == HttpStatusCode.OK)
        }
    }

    private suspend fun uploadImageTest() : ImageForUploadReceive {
        val imageContent = "/9j/4AAQSkZJRgABAQEAcQBxAAD/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQgKCgkICQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAEdAW4DASIAAhEBAxEB/8QAHQAAAgIDAQEBAAAAAAAAAAAAAAYHCAEFCQMEAv/EAFwQAAAFAgIEBQ0MBwMKBQUAAAABAgMEBQYHERITITEIFDd1sxUXIjI2QVFUcXJzstIjMzQ1dIGTlJa00dQWGFVXYbXTCSSVJSc4QkNERVKRsRlHU2KlVmOChKH/xAAcAQEAAgMBAQEAAAAAAAAAAAAABwgDBQYJBAH/xABGEQABAgMDBwcHCwIHAQAAAAAAAQIDBAUGEXEHEjEzc7HBCCE0NTZBshMUMlFhgZEVFlJTVHSSobPC0SJyFyZCQ4Ki8OH/2gAMAwEAAhEDEQA/AOqYAAAAAAABg9w+BFQlvGpUaCS2yWpBKU6Sc8jyPZl4SH3n4BVXhvrxQRhjbvWrVcpVE687xjqFr9dqdW92+p26Olo79meQ+qSlfPJhsDORud3roQ+SemvMpd0xmq7N7k0qWe41Uv2Yj6wX4D88aqZbDpqPrBfgOR+t4Y3/AD4t/wDyInfgXPcIleOEdOJSr/Oi9S5el1a43xbW5J0M9b2Olvy74305ZlZSA6N5djs1L7k0qc9J2p87jsgebuTOW69dCfkX643Uv2aj6wX4DBzKkX/DUfWC/ARnjIqoFc1odWVXAiyTTP6sHRDkk7xrQb4prTi+7ar3/tdmnq9LvCM7Ll46yKC+7ccmuvRabW6CxTKdUKS2469BeqaDN+QtbZuKdRF1ZqPMjbUSjV2Rdjy51hZgplQ79Ob+sF+Azxuo/s1H1gvwFXKPiTitW7jYt+pS6jUJaToU+p0yTbyG0019dcS1ISyrVka2kMJMyczVlomvTP8A1dlHvvHWkWqiZXHbhnyavSIUpTrNIQwqlSVzltOF2MZ09AmdBSiU06ssjURER7ALI8bqRnspqD//AGC/ABy6kWzqYj6wX4CGcAqrf1y3K/dF/UidCmy7Spkd7XRXGW1SGqhU0qMkrQjJZt6lZlopPJaT0SIyIafhKLxHTdVM/Qs7gKMcH3bqdrdDT1it+hszyyGprVU+R5RZryavuVEuTTzqbSj035Wm0lfKIy9F53aOYn7jVT/ZiMvlBfgMFLqWeXU1H1gvwFI9ZjvllpXsX1kS/wAGpeIyrnqv6aHXzj8RI2uqOt0NPWF2unszy8A5qmW2SpTbJTzV7c5br10J+R0NSsatOlHzXnTHZvci86893MT/AMaqX7NR9YL8BjjdR/ZqPrBfgIBxmVUyvm7CjuXoVU/RSGdplRzn6k6prJfea/u5npcX09bs0ctLsR74V13F5zEdFx3nbtZZo12lIhIJx9S2IbkVJFHcKPlnHS6lqSpSlZaRuNF4CHdnFE78bqO4qajP5QX4A43Uf2aj6wX4CBsUqnftCxtcqNpuV5ByKDSGIkeNSFSodQf6ovk8w89oKSyRMrNRqJSDIslGZknRNfi39whpMS5HqtU0Uh6O4ZcUKlyXXYppmkkksLKApskrj6WSzOV2RpVsTnkBZkpdRPdTW/rBfgMcbqRHtpqPrBfgIfwRqN4VO+biqN11O8GG6nTqXNgUmsRG0MtIOIyl5ZLQylKXSdSslIJZbVKM0Fsyh7h+OY3ordnFhIu8UsnFmcd6g8Z0dLTb0NZqdmeWlln/ABGGPF8gxX3Xm4oFI+XagyQ8q2HnX/1O5mpcirz/AAuLhcbqX7Nb+sF+AxxupmeXU1H1gvwHI05HDPL/AGmLn/yAsZwGHeEEvF2oFioq+zpPUN82+rnGuL6/XM6OWt7HTy0su/ln/EfJCn/KvRmYqXna1nJstHkIs95/BiZiX5rV51500F6ONVL9mo+sF+AONVL9mo+sF+Ag3HunYmzbzgW5ZEuvMQrwpaorsyC44lumSYLipKFmtOxo3kqNoz2aWilJ57gnU+oX/XmqXduIabvpdDuVqs1FqI21UMoE/XMtQWpTUQ0yENcWbWtKCNKTWtZq7I0jYkZFouN1H9mo+sF+ACl1E91NR9YL8BWyDdWNMyr2jeDtl3DGpdsQKXFq8VMl4ieXLQXHDVHczdkmyhyMpK+yUlTbpGeZqH6wVXi5Sb5tS3btO55dJmRq3WEzZpvLS0a3kp4pIUrcaF9myS/9m7knYgwBZHjdRP8A4aj6wX4A43Uf2aj6wX4CDMa5Fyx7zqj039IDprVuNOWw3T0VFUR2qE68chEgoKkr0zQUYm9NRJ2rMsz0h8+HVr4sV7E+TeNXfnUOnQaoy7JYk1SY4qWyujsJVDRFWWoJpMlxTmuJRq021FltMwBPfG6j+zUfWC/AHG6j+zUfWC/AVx4Tir7bvOK9asuuNkxbTzsJmAzUlnInlKQaW2lRVEyh40EZEchK0ZHtTlpDQXRR8fpEi7lUuVc7dDlXeiqJdRKcKRHRHkNNpiMII9M47yVtuHoFq8mHs+32gWtOVUi2dTUfWC/ABy6iX/DUfWC/ARfjPVE0a8cOKi2/cSHEXCaZSKc3MdjnDVGeSs5CGCNs06w2cjcLYe0txmIsq68VjlXF1GXenVfVV39ICTxnUpjdUWeJHB0vc9bxPXavUdlv0uzyAFozl1LvU1H1gvwAUqpn/wAMb+sF+ARMEDqCqHWzcVWFUbq5IKgHV9fxo4Gg3lpa/wB20dbr9DWdloaPeyFNuFU9wiW8dbjTYy7+KilxbixUzjfFvg7eloavse20s8u/mPnmY/m7M6681dXqfyVASP5NX3rdc3T38/5HQQ5VTy+LEfWC/AY43U88upqPrBfgOUSX+FznsXint+Xi5/AWcxNcsu5eueq4+OFVUcW6t67WarUpz0Nbt0c892zMfPAnvLPRmaqGspdpflOZSX8g9l6LzqnNzFj+N1H9mo+sF+Azxqo/s1v6wX4CCqxPxAt/EWo2u9FuWXbdHlyb5KfHJ5zjEUmD0KUhZZmtfHDWsmiz9yShOWR5BAqLuNdOtVNtXtDuCDIm12lV16aT8ye2hp83CmxzVCUl1LKHUpUTSVlopdIu1SNgdOWz43US305v6wX4A43Uf2aj6wX4CPqt1RLg+1ordkTH6gVszyhuxES231P6hzQNon1KkEvTy0dJRrzyyPcI/wAK6PjPTsTrXTfUutuUmnW7UKQypySp1mcllURbM6SRGZFIcJ1SMl9nmy4ZbFACwPG6jv6mo+sF+AONVH9mo+sF+AgO8KtcES9sVqPZj92KmSbVhvQkpamrZTNSuVxgoi3C1KXdSbGSWzLNWjsM8wp1dy6yhTOLO4i/oXxisfo+pg6h1QOXxKJxMl5/3jV8Z49oa3sNLR0ux0QBajj8pt1tEqFq0uq0CUlwlZK7xGQ+4L9JKrfo9Qer+j1U1cXjujllxjVlrMstnbZhgAAAAAAAAAAflSsiGuYZqcZJtMMxnG9NakqU6pJ5GZnllon4QtY1OPt4c1BMeZLiqekwI6nYshbDpIcmMoWSXGzJaDNKlFmkyPbvHl1lLKPfNu77Y1f8yAG/Tq/ikX6dXsg06v4nE+nV7IUOsnZPjt3/AGxq/wCZB1krJ8du/wC2VX/MhcBv06uZZHEi/Tq9kGlVvFIv06vZCh1krJ8du/7ZVf8AMg6yVk+O3f8AbKr/AJkAbiiWlTLZlzp1u2jQ6bIqbmumuxUk0uQvMz0nDSjNR7T3+Ext9KrZ58TifTq9kKHWTsnx27/tjV/zIOsnZPjt3/bGr/mQA3kqrF/ukX6dXsg06x4pF+nV7IUOslZPjt3/AGyq/wCZB1krJ8du/wC2NX/MgBv06v4nE+nV7IxpVfPM4cT6dXshR6yVk+O3f9sqv+ZB1krJ8du/7ZVf8yAG/Oq+KRfp1eyPgrtDbualvUW4KFTKhAkZE7GkLNba8jIyzI0ZHkZEfzBf6yVk+O3f9sqv+ZB1k7J8du/7Y1f8yAN3btsxLRp5Ui2LbpVMhEtThR4yzbQSj3nkSN5ja6dW38Ui/Tq9kJ/WSsnx27/tjV/zIOslZPjt3/bKr/mQA36VWLdEi/Tq9kGlVvFIv06vZCh1krJ8du/7ZVf8yDrJWT47d/2yq/5kAN2lWPFIv06vZAZ1ffxSJ9Or2Qo9ZKyfHbv+2VX/ADIOslZPjt3/AGyq/wCZADcSqvv4nE+nV7AySquX+5xPp1eyFDrJWT47d/2yq/5kHWSsnx27/tlV/wAyAG/Sq3ikX6dXsj4K3REXJTXqNX6DTKhAkEROxpCzcbXkZGWaTRtyMiP5gv8AWSsnx27/ALZVf8yDrJ2T47d/2xq/5kAbq3LXh2hA6lWvbdLpcPTNwmIqzQ2Sj3noknLMx9FXpB1+nP0it0SmzoUpOg9HkLNbbic88lJNGR7Qu9ZOyfHbv+2NX/Mg6ydk+O3f9sav+ZAG4tu06fZ0JVNtW2KTSoi3DdUxFWbbZrPLNWiScs9hbf4ENvpVczz4pF+nV7IUOsnZPjt3/bGr/mQdZOyfHbv+2NX/ADIAbyVVi2cUifTq9kGlV/FIv06vZCh1krJ8du/7ZVf8yDrJWT47d/2yq/5kANxLrBbOKRMvTq9gZ0qtnnxSL9Or2QodZKyfHbv+2VX/ADIOslZPjt3/AGxq/wCZADea6v3okX6dXsjBKq5HmUSJ9Or2Qo9ZKyfHbv8AtlV/zIOslZPjt3/bKr/mQA36dXP/AHSJ9Or2QGqrGWXFIv06vZCh1krJ8du/7ZVf8yDrJ2T47d/2xq/5kAfZLw6tqdcSLul2RQnK22tLiagZf3glJLJJ6ehnmRbCDER1Yv8AdIv06vZCj1lLJ8du77Y1f8yDrJ2T47d/2xq/5kAN+lVu/Ei/Tq9kYzq2fwSL9Or2AodZOyPHrv8AtlV/zIz1krJ8du/7ZVf8yAGs2ai+6zxhphttpzWHoOGozMiPItxeEbBKiVuCJ1k7J8du/wC2NX/MjOBz0mThfRHZkyVLdSmQ1rpL63nVJRIcSnScWZqWZJSRZqMz2bwA+D8rWlBGpR5ERZmY/QSMZ6FWblw2rFFoURcuVIJg1REOk2qWyh9tb0clGZERuNJcb2mRdntMi2gBxKUwbWvJ1GrMtIl6RaOXhzGCmR82yN5v3XtOzLsvJ4RX+JY1clYb3Lajtl1ij06ZWV12kU5qPGkNsRUyWlIguRyeSk0rU244pklEnVumnSI9hLtMw5vCLQLQte5cOZUiQqsSqq5XIcONrqFC6om/Hgx0k6ZsKNJpSegpSW2yUkjWeQAm/GrI8PJXOFL/AJhHD0Itx5pVZl2m1UIdzyoESLUaYcmC3HYWiYR1CPkS1LQa05f+w07xKKc8toAyAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKOJF+nYVJjS49Eeq8+oyygwYTclqMTrxtuOZKddUlDadBpZ5me8iIiMzIh8GEOKjOKdAOqKt+XRZbbUZ9yJIdbd9ykMpeZcStBmSkqQrv5GRkZGRd8B9AMHsHwVCv0OkLYbq1YgwlSVaDJSZCGjdV4E6RlpH/AgBsBhR5FmAlErceYFbgBDVaxexEojVSrUnD1MmilVnaPBVEN5yWy6iSTCXJLWh7072yXGzUSSUjS2GakzKW4hUi5ZVdfxnOlO4gwnaZGqjbMKrLlVZs6U+qcp5cc9UzxJxw0upjaLrxZJQhJp2qSdty3EAMhAwH5K6N50v706H8IGA/JXRvOl/enQA/jBkRjIAB+dFPgGdEvAMgACLjURFh5Ly/aFL/mEcPQRca+TyVzhS/5hHD0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfBXa5S7ao82v1qUmNAp7C5Ml5RGZIbSRmo8izM9hbi2mAPvAEe0MY7HvmrqodBl1DjaWnHSTLpkiKS9UpKXkJN1CSNbSloS4guyQasjIjDwAIt4Q8aZLsNLJQWpNKOeyqs5xoch1qERKM3Gm5vuBrJwmu2IzJJrNJGoiH5wCnVupUSpzZkiTLpK5SEUibOahNzH2EtJJWtKH7lopWRpRsJWiWRlsIz/HCJjxpNqUrRiyZtTarDT1Jp7NNaqCZ0tDLytU4w6422tBNk6vsnEaJtpUSiMiGp4MEyl1a3qxcEclxqjWJEadNglSGqaywlcZBsraYaddRorb0VGsnFmo8yMyNORATYe4Vix4pVoVPGGnN1mhSbjdU3TScpyKFHlrW6hcp1iMxJfeQlgnkokG6RpNKkM5aSDMs7Oq3GK24+2vcN34lUukUyz6SvXR4cdFSmWu7UUusKecOQTkpLiExiYJKFkkzJa9NRIPM9gEz4UKpysNrYOkVKXUIXUmNxeVLSaXnW9WnRUsj3KyyzINatwXsP1y1WRQlVC32qHJ6nRydpjSdFENRNkRtJLvJTuIu8RBgVtLIAVEqVYVIxWqdJh0mbItSJVnp1XoMe5nicaeRUGm1PuwiZyQl113jCGtcSXUpWrLMzI7eJ3EK7XJi/cz11TaCyuiwXkV1mnpoDDUtuvS2ESUpKQh0i1erWj3TtFI1ZmSnEnmabEEezcYA/QQMB+SujedL+9Oh+LcEHAfkro3nS/vToAfxjMhkJ+LN1VKyrDqVw0dMbjrSo8dhcojNllTz7bJOuERkZoRrNMyzLMkntLeAG/MvCDMvCIYYxDxFfsK5DpzcKq3FblwLoypMOGakvxkOtayUiLrM1rQ26ojbJe1bZkX/KEKNwjsRJ9AlViOxTYirahTp8xqZAWh2tJj1NcNLbKNZ/d1KQ2aj98Mlutpy3kYE1418nkrnCl/zCOHoRdj1cjNOtNmju0uqvrqNRphJfjQXHY7OVQj7XXElotl5xkJQI8wBkAAAAGDMi3mMhWxK48q1HWKZeH6LypMuHGaqZNtuKbNyS0jQSTiVJ0nNLVpM0mRGsjy2ABn0izyH6EL4SSqVWr8qdYpOMVfu5uPSm43FatEbYJKTfUaZLBtsModaVoqTrEksjNOxWW+aAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVsUVMow6uVcm3HK+ymlSTXS2zUSphatXuRGkjURnuzSRn4CMw0hZxLcnNYf3E7TK8xRJSKZJWzUX16DcRRNmZOKVkeiRb88jy35GAIN4PhW61iCtmjLoleNdLkyF1GjVOoymaa4t1k3GnkylrSTz6slmsjStZsqNSe+LLlkRCtnBxksuXVIXTMR1TinNTZUuhvXBMq+oRpRyY1T8lJZqbM39YSMtHXtoUXYkYlrFvEdWF9tM3D1KKoE7LRF1Wu1eWklSs88j/AOX/APo+ebm4UjAdMR1uY1L1X2e4+iVlYs7GbLwEve5bkT2+8+bGmXa6LbhUq57OK6OrFSahQKabjTROytFa0mbrikpayQ24elnn/qkRmoiPxwjrLNSl3FAXbUe3pVJkRYbtMabYPizaYyNUnXsrUh1Oju2INBdiadxnD9f4UdCummPUS5sKoNVp7+WtizZCXml5HmWaVNmR5GPO2+E1bdnU0qPaeElNo8FKzcKNAfQw3pHvVoobIszyLaOY+fdB+v8A+rv4Oj+ZFe+o/wCzf5LVqPMtggfGzBt27ryjXe9cltoYTGZYTTK7GddbkqaN0zY7B5BG04byVqRoKM1sMntIsg2YU40U/EenTqhOixaKqI+TKW3ZiVG4RpzzLMk+QaDEjDanXndjt0wLlsKQcumtU12NctHRVER0oW4rTj5Pt6s1aw9IjzJWgjdkOkkp6BUIDZmXdex2hdHs7zQTchMyMd0vMMue3Smn293MSNhrT10rD63KYubLlqi0uMyb8uOth5zRbItJbbnZoPwpVtLce0UbxU4c2N9nYk3PalJRbhwqRVpUKPraetS9W24aU6R6wszyLwC8FoHbto2pSLYTdkacVKhMwykyJaDce1aCTpK27zyHJnHxSF42X0tCyUlVfnKSojzIyN5W0jHzVOM+Exqw1u5yP7cTs3TpWE+XcrFV1y3c3cS5/wCIRj7/AOja/wDhq/6g21p8PjHas3VR6PLbtvUT6hGiu6FPWStBbqUqyPWbDyMxVA9wYMOuUC2OeYPToGobNx1cn9akcy1oqq+Mxqx3XKqd/tO1qd2/MIOA/JXRvOl/enQ/FuCDgPyV0bzpf3p0dWT0P4+ao06DV4T9MqcNiXDlNqZfYfbJbbqFFkaVJPMjIy3kY+kAAV28McPWqWdEbsihIp5xThHFTAaJo45uaw2tHLLQ0+yy3aW3ePZ3DuxH0Uhp+zaI4i3zI6UlUFsygmWWWpLL3PtU9rluLwBiAAEXGoiLDyVkWX+UKX/MI4eSLII2NfJ5K5wpf8wjh6AAAAAACPce2m3sLasTtJcqBIciOEht2Q2bBplNKKSao3u2TJkTxk32Rk2ZFvEhBVxPoFfueyajRLYn8TqEjVG2s5TsUnEJdQpxk3miNxonEJU3rEEak6eZbSAEU8HO4JVwVtbUy2GKeVFt6NTYyWW5qDgMIeWSIrxySLWLUlCHSWkiPRURKLcpVgBCeB9m3rZd1VSm3nciZshdMZcW2VYmTzlKN93KQZSEkljRTkzooM9LQ0lZbBNgA/OmQySiPcIE4TVERVnaVKVe8Wkpp8V1a4byppuPkt+OkjabiKJbinD/ALsaclHoSVGks9hvGAjLLOGNLKNUo8xlb0txCY5Pk1EJUhw+KpJ8idImc9VktKVFoZaKdxASIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAF7EBi2ZVlVyPeb5MUJcB8qi6a1I1cfQPTUSk7SMizMjLbnuG2qlSi0emyqtPcNEaEyuQ8skmei2hJqUeRbT2EYrtW+G7wTbjpEyhVq8H5UCoMLjSWF0eZk42sslEeTee495bR9MvJTM3f5CGrrtNyKu4+WYnZaUuSPERt+i9UTebfCWBY8PEVlNvP3ct9dNnykJucpUd01PPMKkPx2nmUk7rV6KnVaeaD0MkESzH38LPkzh87M9G6I2tbhP8Dm0qt1ch4iXNUJ6Y6ojL9W6qz1R2VGk1Ia15K0CUaEZ5bT0U5meRD5MY+E3g3jJazNrYfXK/UKixMRNcaXAfYImUpUk1aTiCI9q07M89o0dr6ZOwaFNRIkFyIjFvVWrcn5G6shVZGNXZWHDjNVVelyI5L95C/e+cYGe8f8BgVavLSN0EI8IBSk1ik5GZZxnN3niKdYv/AJ1f9RKvCB+OKT8lc9cRSJms11VBwXepIdHhsWRhqqJ371Mm45/zq3eENEv34vRt+oQVT3/MGqX78Xo2/UIbl2sTBeBV3lbNa2h0+5P913gPA9wYMOuUC2OeYPToC+e4MGHXKBbHPMHp0DMz0kKLynSGYpvO1pbgg4D8ldG86X96dD8W4IOA/JXRvOl/enR2ZZNB5RMiuSXIbchpT7RJNxtKyNaCPcZlvLPvD2FdsYodmVi+6tVrkueVQ3rQj095CKGluDWJ2uUrQbTL09a60ai0SaQlJGrNOatwmRVSjYd2Qc+87nemMUePnKqUlota8kjySakNl2SzzSnJKc1K3FmeQAZQBPaxXsh20H746pvopkWRxN8nIT6JLcnWk1qDjmgnSdNxSUkjR0jNRZFtIal/H7DCPFhS3q5KS3N1xn/k2TpRSafJh1UlOrzjpQ6okGbpJIj8hgD7Ma+TyVzhS/5hHD0EXGkyVh5Ky/aFL+/xw8keYAyAYMyIszH51qNPQNRaWWeWe3IAYekMsJNbziW0FvUo8iL5zChiXT1XjZVSt2h3LFp8yUlvQdVJUhCyS4lamlqbUS0ocSlTajQZKJKzMto1XCHLPCGvnl/qM9MgUc+ccJae2brOzbZZIOfe1HX513eqepfUdzZixzbRSrphY2Zc7Nuzb+5Fv0p6y5eEdkTLIrVUqs5y2qHBmxmY7dGotUkSo2tQpRqkqU+SNFZkok5IQWZF2Rq2ZSwzPhyVGmPJadMt+gslZf8AQc3BYPgekR3FcOfiTPrmNfRcoLqvPw5JYCNz1uvzr7uZV0ZvsPvrdgG0eQiTqTGdmXc2bdfeqJpzl9ZIOLNNmz8ULTnUzDk7mkUSBKqZKRW3YDrZofjklCEbGHz0jJWg8pJFoZke8SBh6iAVupkwKBVKKU2XKmPQqmnKS3IdeWt01dkotq1KMjSo05GWieWQSsb7FxLvGVSXLCryobEVt1MppNYkU81KUtpWlmylWs0mkPM9lloG8TieyQQYsGGKlEsONEqtSKZIZlzW9k92ccdJSXCTHOQ6RLeNosmzWoszNB794ksjceTPIhEt68Iij2hdE+02rSrVVlU/S1jkZyK22am4yZTyS1rqVGaGFoXnlko1aKTMyPKWj3Cq+O1HvSoX/Vn1YewplMWhlmNOTa8KouPkhglR0m48vSUapRuNrRo5IaSlRGnSNYAs9S6hHq1Oi1OGpSo8xlEho1JyM0LSSizI92wyH1j4qMqaqlQzqUdpiWcds5DTXaNuaJaSU/wI8yIfaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABbxKIzw6ugs9vUab0Cxw6LcXkHcbEjk8ujmab0Cxw5T2peQSNYRL4cfFvEja3mtg4O3oZEj4E910rm9zpGxHAkfAnutlc3OdI2MmUxESyNQ2a8DDkzT/ADdT9ohOwAAHn7pPQQhDhA/HFJ+SueuIpErcIH44pPyVz1xFImazXVUHBd6ki0boMPBd6mD3/MGqX78Xo2/UIKp7/mDVL9+L0bfqENy7WNwXgVb5XHUdP2rvAeB7gwYdcoFsc8wenQF89wYMOuUC2OeYPToGZnpIUWlOkMxTedrS3BBwH5K6N50v706H4twQcB+SujedL+9OjsyyaEYYyWzijdeKTVSsiY09+jiorTEeKzT9fGU6ROa55yS0pxLZ6K0q1SiVoqSaSMyMTPiJaTl82hNttioFAkPKYfjSVN6xLT7DyHmlKRmWknTbTmnMsyzLMt4g/He1jm4lJrVCt9NSruqgNttqsWXMS8lLpmSFVJDqGWiIjUZaexB7TzzyFlUFs8oAhmZgpdlUs+4bbqd1Uhx256j1Zn6NOcJlcvXtK1OjrdLi5ssIbMsyWZmpWkWeQWJ3BWqk2m0eAm6qW31PKWSCKnummmG/NTJJyD7tmlSNEkFrTcLJKTy3pOx4ABF2PFtU2qWk1WpbtQKTTKjTFMEzUJDLRmdQj56xptZNu+RaVCUCLII2NfJ5K5wpf8wjh6AHjK1XF3NevRb0T01aWjkWW08+8KwYM2zhfScT2bopN51ipodjtU2iMVV2qNz1G6T7yXnEyF6EhhTaFk28SNE9HvqLSOzs9CnIbyEMpeUptRE2o8iWeW4/LuFYsPbmk3niVaVGnYcNwzst3icWG/Qai1Jo6Cp+Tkgqg4omXG9YpTBM5KM+xWSjMiMAS9whuR+v+Yz0yBRsXj4QvI/X/Ma6ZAo4IMymdaw/7E8Tib8mfVkT+9fC0BYTgemX6R3Ft/3Jr1zFe8zLcYjnGuvV2gUqmvUKszqc45IUlaoklbJrLR3GaTLMhzlk4vkKzAiXX3Ku5UO3qdFfaGVdTIb0asS5L1S9EuVF4HR/F/Dy470qFHm0yHTqzT4LchEmjVGrSqew644berf1kdKzUpBIWkkrQZZOGZGRkGXC62q3aFlwqDcNQRLmMOPrPQfdfQw2t1S22EuOnrHEtoUlBKX2RkkjPLcOLvXGxC/+vLi/xN/2hksRsQc9t93H5eqj/tCffldv0PzOS/wInvtjPwr/ACdzNIlZkXeFRMfIVLnYoVGNFqr05k0yOqMCVablVRGfVBjm87Ed17aG3morLLpZko06xWjpGs0lIXAcqNQq/BpteoVWoSZsl12dpvSHVOOKylukWalGZnsLIfNi6zhxVq5dEs8HplxVKixUt1itsobJqEvi5OJJxJyGnXiJlaDWlss1NmSDM+1Lawn+VYj/AFkK1WQdSp+NIvW9YbnNVfXmqqX++4nK3eKHQqccCY5LjHEZ1Mhw81vI0C0VqPvmZZGflGyGuoD7EmiwJEVbSmHYrS2lMtm22aDQRkaUntSnI9hd4tg2IyHwAAAAAAAAAYMaCpX/AGTRp8Gl1e7qLCmVOQuLCYfnNIckvJVoqbbSas1KJRkkyLaRmRb9g36s8tmQp1Xbjsaj3tftBXSqvObq0h9uYuRWICFwo7dRNUg4jJt60lnJlGpDazNTiclJUWSSAFxSPMt2QyPyncPy++zFZckSHEttNJNa1qPJKUkWZmZ94iIAegAjW9jjhBd1xN2na+JFvVarvMFJaiQ57bq3G+y2p0TMlZEhRmRbSIsz2ZB5AAAAAAAAAALmJHJ5dHM03oFjhyntS8g7jYkcnl0czTegWOHKe1LyCRrCauNi3iRtbzWwMHcDIkfAnutlc3OdI2I4Ej4E91srm5zpGxlym9kZ/ZrwMOTPtdT9ohOwAAHn2h6CIQhwgfjik/JXPXEUiVuED8cUn5K564ikTPZrqqDgu9SRaN0GHgu9TB7/AJg1S/fi9G36hBVPf8wapfvxejb9QhuXaxuC8CrfK46jp+1d4DwPcGDDrlAtjnmD06AvnuDBh1ygWxzzB6dAzM9JCi0p0hmKbztaW4IOA/JXRvOl/enQ/FuCDgPyV0bzpf3p0dmWTQjXFyVRWbsvCJJvl2kxpESlFWKMcZlcuqoNSkslT1qcSpClbW1dgvNRdjoqzM7Dp3CvuJNv4szcSKPVrhpsmdacKsGaHLYbaKUzANlzRJ5Sv70S9dqNI2F6JoJeZF37ApPIj8oA/YB+dMtn8QEoj7+4AI+NfJ5K5wpf8wjh6CLjUZHh3Ky/aFL/AJhHDyZ5FmAPKUhbsdxpt021rSaUrIszSZlsMV0sDCC+LEv23ahcd0Q5STlKRrmajUZLyyTEcSuOaHNJskPOEclalmWi52Cc+xMWHfeZfZcZTLS0paTTpJUWknZvL+JCDsPcC6taF00OsSZFqRjoqnDlVWmtSE1KuaTS28palrNOSlKJxWZrzWhOWiP25T8zkHPhCllg/XvMZ6ZAo4Lv8IJ9peEVfbStJnq2dxkf+2QKQbctogzKYl1Vh/2J4nE4ZM1vpcT+9fC0BFXCAL/ItJ+VL9QSr4PKIr4QHxLSflS/UHLWb60g4ruUlekdOh48CDswbdgAZ5Zf9BMxI5e3gy8M/DfB7Beg2FcNCuCTOgqlLcciNMqaMnJDi05Gpwj3KLPYGO5OGFwULvq6a9c+EFXqVQJKGzffgRlKWlPapX7rksiz2ErMhQ2KWcGP5h+sY9MiH3Q6jHhtRrbrkPLvKHbKpylrqnAh5ua2YjInN3JEcnrOjSf7RrBlpBIatW7CJJZJSUePkReD30WopFQbq1LiVVglE1NYbkIJW8krSSiI8v4GOHuRFtyHbKye4ugZd+lxeiSNpT5qJMq7yndcLIV+crT4rZq7+m665LtN/wDBvAAANmduAAAAGFHkQrTcz17VO7609O6hUmdQKolyiMuN0taq9LVJc4u24a1KeQaYZNERlq3M1OGWZJIjsqvtRVy/sN8MY2IFTUvFGn9XatKMnKAlmAuoLbkyGnnG21uqSsnTWhBtrUeaEZpSR9iALSJ7Uh4TzUmE+pEXjKibUZM5kWsPI+x27Nu7bs2j3TuHw11M9VFnppb6GZpxnSjOry0UO6B6CjzzLIlZGAK74F3AiZjBWIFKwonW2SaNEKdFlVph0qN7tKNLKYpJ0myUvS2JUaMlEaS0cjOyxCt2Et0124rxoNWqGKVGn3BUnpCKrQ6fXIsuIVKajuIbcaShCVKd4whCjUW4luF2pFlZIAAAAAAAAAAuYkcnl0czTegWOHKe1LyDuNiRyeXRzNN6BY4cp7UvIJGsJq42LeJG1vNbAwdwMiR8Ce62Vzc50jYjgSPgT3WyubnOkbGXKb2Rn9mvAw5M+11P2iE7AAAefaHoIhCHCB+OKT8lc9cRSJW4QPxxSfkrnriKRM9muqoOC71JFo3QYeC71MHv+YNUv34vRt+oQVT3/MGqX78Xo2/UIbl2sbgvAq3yuOo6ftXeA8D3Bgw65QLY55g9OgL57gwYdcoFsc8wenQMzPSQotKdIZim87WluCDgPyV0bzpf3p0Pxbgg4D8ldG86X96dHZlk0IyxDxLxDtW9LiosrECixKZIlxWo6ItDkSZtLiqZ03HzPWJbMkpLSUsyUkjUnsduiJTxmqVcpWGlZn29Ilx5bSWSXIiN6b7EY320yHW05HmtDJuqLYe1JbDET4kU+/rhxberNk2rFrdMnUt2gIqDMKnuMRZDclvWJmOPFr9Ftxt3NCdJORbEmvaVjiTmQAra/iBW28ILoiUS66tJdYrBlQqm8bpvuUY5bDZvuSNE16ojU+g3yJSybTrC0siM0lWIuLUWFbL0mr1GTTY7VSkREJkS9Ovy2qkTbMRiQgkreI45kba3SycSalKQoiNSbkass++M6BeEwBGGO824mbSai02hRpdPfqNMKZKcnapcYuqEfI0t6CtZ39mkkSeW48wj41ERYdyucKX/ADCOHk9xgDh5edWqqLxrxJqcsiKpysiJ5Wz3VX8RpurFX/akz6dX4j7r07sq9zpL6ZQ0wn6DCZ5NvMmhO4r5EiP8o7nXSo/YQ1Kov31CafnyXEG2/mlbqlEfuau8ZifxXjBvu+g+jf6JQsOKf8oJqNtHBRE/2W+J5cDk+qrrOR1X65fAwPB5RFfCA+JaT8qX6glTweURXwgPiWk/Kl+oIms31pBxXcpYmkdOh48CDhg+95RkYPveUTKpI6G/ifAY/mH6xj0HnE+Ax/MP1jHoCHkflN7aVb7zG/UcB7h2ysnuLt/muL0SRxNPcO2Vk9xdv81xeiSN1R9L/dxNlk61kxg3ibwAADeEogAAAAv1O/bKpMx2m1S7KRElNZE4y/MbQtOZZlmkzzLYZGIgq1r4bVSZVIycboUe3q3UTqk+kpXAWbjynEuLJMhSTdQlSkke/STuSaSIsoa4QxF14a/nu02OgQI5y/iQiOoZR5qSm4ss2A1UY5W33r3KqEsU3J3KzsnCmXRnIr2tddcneiKdA28TcPXFpabvahqWoySlJT2jMzPcRbRvKpGiT6bKhTvg0hlbT2atHsFJMlbe9sM9o50U6QzDqUSVJdJtlh9txxZ7kpSojM/mIhbiXwsuDHPhvQJeLVCcYkNqadQZuZKQosjLte+RmOnsnap9oGxXTDWszLrrl033+vA0torDzFMdDSntiRkdffcxVuuuu9FF08+kU8MqPhPExBoUWzMRqrcrjcl2aTElbDTJutQVRUSm1ahs5BJjklnRaUpORk4ojMjULKkKtW3ivwV7eqlInP8ACOk1iNbpK6iwKjLJceAZtKZI06DCVuKJpa0EbqlmRKPvnmJatHhHYJX5XotrWfiNSqrVZmmbEVg16a9FJqVlmki2JIz+YdgkeE5bkcnxOXi0GrQWLEiysRrUS9VVjkRETSqrdzISYAYLcMjKakAAAAFzEjk8ujmab0Cxw5T2peQdxsSOTy6OZpvQLHDlPal5BI1hNXGxbxI2t5rYGDuBkSPgT3WyubnOkbEcCR8Ce62Vzc50jYy5TeyM/s14GHJn2up+0QnYAADz7Q9BEIQ4QPxxSfkrnriKRK3CB+OKT8lc9cRSJns11VBwXepItG6DDwXepg9/zBql+/F6Nv1CCqe/5g1S/fi9G36hDcu1jcF4FW+Vx1HT9q7wHge4MGHXKBbHPMHp0BfPcGDDrlAtjnmD06BmZ6SFFpTpDMU3na0twQcB+SujedL+9Oh+LcEHAfkro3nS/vTo7MsmhrKRwfrVhTqjVqjWLifmT6zNq58Vr9QiMIN+Qt4kahp8m8k6RJPsSJWRmZbTEokWXfGQAAAAAARca+TyVzhS/wCYRw8nuCNjXyeSucKX/MI4eT3ADhjendlXudJfTKGmG5vTuyr3OkvplDTCwMHVtwQrzE1jsVHbBvu+g+jf6JQsOK8YN930H0b/AEShYcU+5QfaSBsW+N5cLk99nI+2XwMDweURXwgPiWk/Kl+oJU8HlEV8ID4lpPypfqCJLN9aQcV3KWKpHToePAg4YPveUZGD73lEyqSOhv4nwGP5h+sY9B5xPgMfzD9Yx6Ah5H5Te2lW+8xv1HAe4dsrJ7i7f5ri9EkcTT3DtlZPcXb/ADXF6JI3VH0v93E2WTrWTGDeJvAAAN4SiAAAAFHOERywXB57HQIEbiSOETywXB57HQIEbirdc60mdo/xKWes/wBVS2zZ4UPCf8Ak+hX6pioIt9P+ASfQr9UxUHweQdhYT0I+Lf3EiWY0RfdxMHuFgOAd/pQWn6Of9zdFfz3CwHAO/wBKC0/Rz/ubwkaW1zMU3n0Ww7Oz2xieFTriAaK7rzt2xaWVZueocUiKdSwk0srdWtxWeSUobSpajyJRnkR5JSozyIjMvlsjEW08Q40mXalUVLaiOIQ4a47jJmladJtxJOJSam1p7JDic0qLaRmOuKKjOAAAAuYkcnl0czTegWOHKe1LyDuNiRyeXRzNN6BY4cp7UvIJGsJq42LeJG1vNbAwdwMiR8Ce62Vzc50jYjgSPgT3WyubnOkbGXKb2Rn9mvAw5M+11P2iE7AAAefaHoIhCHCB+OKT8lc9cRSJW4QPxxSfkrnriKRM9muqoOC71JFo3QYeC71MHv8AmDVL9+L0bfqEFU9/zBql+/F6Nv1CG5drG4LwKt8rjqOn7V3gPA9wYMOuUC2OeYPToC+e4MGHXKBbHPMHp0DMz0kKLSnSGYpvO1pbgg4D8ldG86X96dD8W4IOA/JXRvOl/enR2ZZNB/Ggvm7oljWvNuaZDflpi6tDcaPlrH3nHEtNNJ0jIiNTi0JzMyIs8z2DfjS3jalNva3Jts1ZT6Y01Kc3GHNB1paFkttxCtuS0rSlRHke1JbDACa/jXCg4fV296pbsuJItueqmVOnrfa/u8gnEJzU9nqya0XW3DcPIkoMzMiyMhrGuEhb79t23XWKDNkPXFVEU5MaO806lls5hRFS9aR6C4+sUjRWnasllolvy2fWKoi6FNoci6LifKoySqMmSuQzrXKgmQh5Mw8miRrCU22kk6Or0EEnQyHwzODFhRV6TFp9wUNNWmRqgVSOqTG2lzHXuN8bUSlEgkkhTpnmhKUpyUZERZgD6seLrtmk2i1QqtcFOh1Kq1GmJgRH5KEPSjKoR8ybQZ5r3lu8Ik8jzIImNCCTh7KPItlQpeX1+OHvLIj2ADhjendlXudJfTKGmG5vTuyr3OkvplDTCwMHVtwQrzE1jsVHbBvu+g+jf6JQsOK8YN930H0b/RKFhxT7lB9pIGxb43lwuT32cj7ZfAwPB5RFfCA+JaT8qX6glTweURXwgPiWk/Kl+oIks31pBxXcpYqkdOh48CDhg+95RkYPveUTKpI6G/ifAY/mH6xj0HnE+Ax/MP1jHoCHkflN7aVb7zG/UcB7h2ysnuLt/muL0SRxNPcO2Vk9xdv81xeiSN1R9L/dxNlk61kxg3ibwAADeEomDURbwEeZZiMMTKHDqlz0+bWsS1WzDj054oSWqucJ1E7WNmmRqzUSJCSSSkmhwlJ25ZHpHlsMDZU+bYCJVUuyBcspyrVc3ajANfF3D6oSOwb0zMySjtMiM0loZJMyIjMCrnCJ5YLg89joECNxJHCJ5YLg89joECNxVuudaTO0f4lLPWf6qltmzwoeE/4BJ9Cv1TFQfB5Bb6f8Ak+hX6pioPg8g7CwnoR8W/uJEsxoi+7iYPcLAcA7/SgtP0c/7m8K/nuFgOAd/pQWn6Of9zeEjS2uZim9D6LY9nZ7YxPCp0sxkl1GMzayaNaMK4ag/X0Nxo8yauK00rikpSnDcQheR6CVoIlJNJ6zI8t4+nDRMJ96pTut/VbTqSWocGTFmaCmTaYQomSjraUppTaSUouxyMs+yIj2D58bJiINuU56feZ2zSlVeOipzG5pxXlxjJZatpwiNSVmvVmeWRmlKyzIszGmwMrtZr9Wul9/EKn3bSIfEoVMlR1qJ5SUpdUp19vRJKVrS437o3mh0kEtORHol1xRUl0AAABcxI5PLo5mm9AscOU9qXkHcbEjk8ujmab0Cxw5T2peQSNYTVxsW8SNrea2Bg7gZEj4E91srm5zpGxHAkfAnutlc3OdI2MuU3sjP7NeBhyZ9rqftEJ2AAA8+0PQRCEOED8cUn5K564ikStwgfjik/JXPXEUiZ7NdVQcF3qSLRugw8F3qYPf8wapfvxejb9Qgqnv+YNUv34vRt+oQ3LtY3BeBVvlcdR0/au8B4HuDBh1ygWxzzB6dAXz3Bgw65QLY55g9OgZmekhRaU6QzFN52tLcEHAfkro3nS/vTofi3BBwH5K6N50v706OzLJoP4AAAAAAAAi418nkrnCl/zCOHk9wRsa+TyVzhS/5hHDye4AcMb07sq9zpL6ZQ0w3N6d2Ve50l9MoaYWBg6tuCFeYmsdio7YN930H0b/AEShYcV4wb7voPo3+iULDin3KD7SQNi3xvLhcnvs5H2y+BgeDyiK+EB8S0n5Uv1BKng8oivhAfEtJ+VL9QRJZvrSDiu5SxVI6dDx4EHDB97yjIwfe8omVSR0N/E+Ax/MP1jHoPOJ8Bj+YfrGPQEPI/Kb20q33mN+o4D3DtlZPcXb/NcXokjiae4dsrJ7i7f5ri9Ekbqj6X+7ibLJ1rJjBvE3gAAG8JRIixPKOrEClqiYT027ahGo0mQ+5NeShxEIn2iW1GQttSHXjUaVaKlNlkWWl2Q32BtWtuuWAmp2lbxUSlO1arJYhk2prI01CQlazbURG2a1EpZoyLRNRkWwgu43WPipdtVpMmwa65FhxmlJlR262/TVKPWtqXtaQrWG4ylxkjM06o3CcTtSWTTgw3U2cPYDNXq6ajJbfloNxM5c02klJdJDCpCyJTymkklpS1Fmo2zM894AqpwieWC4PPY6BAjcSRwiOWC4PPY6BAjcVbrnWkztH+JSz1n+qpbZs8KHhP8AgEn0K/VMVB8HkFvp/wAAk+hX6pioPg8g7CwnoR8W/uJEsxoi+7iYPcLAcA7ZwobTz/8ATn/c3hABbyEycEa9Law8x7t+77uqHEaVARM17+rU5oaUZxCexSRmealEW7viRIDkbFaq+tN5mto9sOzk+563IkGIqr/wU6m4zQ7IlWqyu+qtUKdHYnNrhv05ThS+NKStBIZS2lS1qUhbidFKTM0qV5SX8A4lnRF3AzZ8qqyGIyoUMjq7rqZjDDTJkzHVHdabWy22kzJBq0jWSjUajMJ14cKDgrXxTGqdVsSZTBxpCJcWVCYmR5EZ9JGSXG3EII0nkpSfAZKMjIyMyHz2ZwluCtZJzn4eKVRqM6qLQuZPqbcyTIeJBGSEmpTeRISRnklJERaRnlmZmOp85g/TT4lBvlen/Xs/En8lmABJwzxjw8xejzpeH9wFVGqc4hqSomHGtWpZGae3SWeZEe4OwzNcj0vat6H2worIzEiQ1RUXvTnQXMSOTy6OZpvQLHDlPal5B3GxI5PLo5mm9AscOU9qXkEj2E1cbFvEjq3mtgYO4GRI+BPdbK5uc6RsRwJHwJ7rZXNznSNjLlN7Iz+zXgYcmfa6n7RCdgAAPPtD0EQhDhA/HFJ+SueuIpErcIH44pPyVz1xFImezXVUHBd6ki0boMPBd6mD3/MGqX78Xo2/UIKp7/mDVL9+L0bfqENy7WNwXgVb5XHUdP2rvAeB7gwYdcoFsc8wenQF89wYMOuUC2OeYPToGZnpIUWlOkMxTedrS3BBwH5K6N50v706H4twQcB+SujedL+9OjsyyaD+PypSUFmpREX8R+gkY0UOs3HhtWaPQYjkuU+lg1RG3SbVLYS+2t9glGZERuNJcb2mRdntMiADmUhlTeuJxJt5Z6RHsy8OYxxqOZoIn2/dO07Iuy8nhFcpFnXi9hPdVmUrD+p06HVKz1UpdPI42UKncbZNUdLOs1ZryQ88Uc/cjSvQUe00jT06wLhj0Gz7TqmFdR02avKnLuFqJGOXTIaagbzDTSEOZRXHSMtImuwbb0iIszJJAThjUZHh5LyP/iFL/mEcPPeEXY8Uqsy7TanQrlkQYkWo0w5MFuMytEwjqEfIlLWk1oy/9hlvEoFuMAcMr07sq9zpL6ZQ0w3N6d2Ve50l9MoaYWBg6tuCFeYmsdio7YN930H0b/RKFhxXjBvu+g+jf6JQsOKfcoPtJA2LfG8uFye+zkfbL4GB4PKIr4QHxLSflS/UEqeDyiK+EB8S0n5Uv1BElm+tIOK7lLFUjp0PHgQcMH3vKMjB97yiZVJHQ38T4DH8w/WMeg84nwGP5h+sY9AQ8j8pvbSrfeY36jgPcO2Vk9xdv81xeiSOJp7h2ysnuLt/muL0SRuqPpf7uJssnWsmMG8TeD86RZ5Zj9Cg39pPdd1W3d1ktW7c9XpSH6bLU6mFOdYJZk6jI1EhRZmWffHWUuQWpzTZZrs1Vv58EvJAqtQSlyrppzc5Eu5sVuLR4v4fXVd9YpVQpUOFW6ZEjvMv0abXZdKaN5SkmiSTkZCzcUkkqTorTkWlmRke9rwxt+u2pZNPoNy1Mp0+PrTW4T7j5NoU6tTbJOu+6Ok2hSWyWvslEjM9pjjR10MTf3jXR/jEj2wddHE39410f4xI9sdX8xY31yfBf5OS+fkH6lfin8F3uEMf+eC4FHu02OgQI4GjsWpVGr2fSalVqhJnS3mDN2RJeU64syWoi0lKMzPYRFt8A3gpRaeD5tW5uCq35sV6fByl1bLR0maHJx0S7OhMX4tQ8J/wCT6FfqmKg+DyC30/4BJ9Cv1TFQfB5B1FhPQj4t/cSXZjRF93EB99I+GKLwtq/wCw+AffR/hp+jV/2HfLoPkyl9jKt92j/puNmAAB+nkaX6/sztts3zn+0IfRLF1BSv8Aszu5m+ecIfRLF1B1NP6M3/3eTzZPqaBgu9RcxI5PLo5mm9AscOU9qXkHcbEjk8ujmab0Cxw5T2peQSlYTVxsW8TmLea2Bg7gZEj4E91srm5zpGxHAkfAnutlc3OdI2MuU3sjP7NeBhyZ9rqftEJ2AAA8+0PQRCEOED8cUn5K564ikStwgfjik/JXPXEUiZ7NdVQcF3qSLRugw8F3qYPf8wapfvxejb9Qgqnv+YNUv34vRt+oQ3LtY3BeBVvlcdR0/au8B4HuDBh1ygWxzzB6dAXz3Bgw65QLY55g9OgZmekhRaU6QzFN52tLcEHAfkro3nS/vTofi3BBwH5K6N50v706OzLJoP4AAAGMgaKfAQyAAIuNfJ5K5wpf8wjh5PcEbGvk8lc4Uv8AmEcPJ7gBwxvTuyr3OkvplDTDc3p3ZV7nSX0yhphYGDq24IV5iax2Kjtg33fQfRv9EoWHFeMG+76D6N/olCw4p9yg+0kDYt8by4XJ77OR9svgYHg8oivhAfEtJ+VL9QSp4PKIr4QHxLSflS/UESWb60g4ruUsVSOnQ8eBBwwfe8oyMH3vKJlUkdDfxPgMfzD9Yx6DzifAY/mH6xj0BDyPym9tKt95jfqOA9w7ZWT3F2/zXF6JI4mnuHbKye4u3+a4vRJG6o+l/u4myydayYwbxN4OeX9qD3Y2JzZN6VsdDRzy/tQe7GxObJvStjvrKdaw/fuU621vVMT/AI+JCkgAAExIQ2hZjDXuCovydXSKDKFrDXuCovydXSKDKPOe2XaOe20Txqei9iuzchsYfgQ8J/wCT6FfqmKg+DyC30/4BJ9Cv1TFQfB5B0FhPQj4t/cSnZjRF93EB99H+Gn6NX/YfAPvo/w0/Rq/7DvnaD5MpfYurfdo/wCm42YAAH6eRpfr+zO7mb55wh9EsXUFK/7M7uZvnnCH0SxdQdTT+jN/93k82T6mgYLvUXMSOTy6OZpvQLHDlPal5B3GxI5PLo5mm9AscOU9qXkEpWE1cbFvE5i3mtgYO4GRI+BPdbK5uc6RsRwJHwJ7rZXNznSNjLlN7Iz+zXgYcmfa6n7RCdgAAPPtD0EQhDhA/HFJ+SueuIpErcIH44pPyVz1xFImezXVUHBd6ki0boMPBd6mD3/MGqX78Xo2/UIKp7/mDVL9+L0bfqENy7WNwXgVb5XHUdP2rvAeB7gwYdcoFsc8wenQF89wYMOuUC2OeYPToGZnpIUWlOkMxTedrS3BBwH5K6N50v706H4twQcB+SujedL+9OjsyyaD+AAVMULtm2RZU+4aZFZkTG1x40Zt9Rpa1z77bCFOGW3QSpwlKy25EYAawCGZuL110zDe5qxKgU2TcVuV79Hj4u26ceW6p1lKXGmdI3FK0X0mTJLzNaTQS9uYU08I6+10ORVU0WjNuW3T5VUrrL7L7TslpmeuKTLLZrzjvGlpa1JWbmio0I26WkQEsY1Hnh5L5wpf8wjh5PcI0xwqc9NqFRKXaterMmXJhvp6mwjeShLMtlxemeZaJ6KVGRd/Ifb120Zcm9+/4Gr2gBxvvTuyr3OkvplDTCZLl4MnCMqdx1apRME7pUxLnSH2lGy0RmhTilEeWs2bDIa39VfhJn/5IXT9E1/UE3Qq1TkY1Fjs0J/qQg2JRKkr1VID9P0VNHg33fQfRv8ARKFhxHWHPB04QVt3ZFq9VwVutuM0h1KlJjtqPNSFEWwl+ExL/W8xY/dDd/1BPtirOXCTmK1X4MenMWKxISIqsRXJfnOW69O/nQtNkOnIFFoMaBUXpCesVVRHrmrdmtS+5e7mNL4PKIr4QHxLSflS/UE3dbzFj90F4fUU+2I3xiwmxfuVVGtyl4S3SdRfU/JaYcioQpxtskE4os15bDcRn5wjKgUGqQKjCiRZd6NRedVat2jAnamWnosKchufNQ0S/wCm31YlaBg+95RKv6qfCW/chc/0TXtg/VU4Spf+SF0fRNf1BK/msb6C/BTvPnrZz7dC/G3+RJifAY/mH6xj0Ejx+DJwjG4zTSsE7p0kJMj9xa8J/wD3B+/1ZuEX+5O6foWv6g/ElY93oL8DzFyg0ydn7WVOalYTnw3x4rmuRFVHNV7lRUVNKKnOikanuHbKye4u3+a4vRJHJf8AVm4Re3/MndP0LX9QdMrZxNdplt0mmy8Nb8S/EgsMOkmiKURLS2kjIjJW3aRjb0qFEhK7PaqaD77CU+bkXx1mYbmXo269FS/SSkOeX9qD3Y2JzZN6VsXP67aP3b37/gSvaFQeHFZmKGOVxWtUsPcIrxlsUqDIYkm/ASwaVrcSZERLWWexJ7h2tmo8KXqTIkZyNal/OvMmhTprTwIszTIkOC1XOW7mRL10oUTAJS/VY4Sf7kLo+ia/qA/VX4Sf7kLp+ia/qCVErVN+vZ+JCKfkSpfUP/CpI2GvcHRfk6ukUGUFl4UYx0S1aZSahg7d6JEVk0upTDQoiPTUe8l5bjIbrreYr97CG8PqCfbFD7V0KpzVdnI8GXe5josRUVGqqKiuW5UW7nRS+Fkq3TZWgyUCPHY17YUNFRXIioqNRFRU7lQXp/wCT6FfqmKgi6dZsXFCHSJ0uXhNdzTDEZ1x1xUFOSEEgzMz7PvERivcXgu8I6bFZmRMFrmdYfbS62smmslJUWZGXZ98jG7sbSZ+TbGSYgubfm3XtVL/AEiR7O2soUukTys5DS+7S9vt9pGA++kfDD9Gr/sJF/VT4S37kLn+ia9sfVTuC3wkY8g3HcEboIjSZe9Nd8vPHaulY93oL8FPlygWqok/ZOpysrNw3xHwIzWtR7VVzlhuREREW9VVeZEEMAkr9WbhF/uTun6Fr+oD9WbhF/uTun6Fr+oP3zWP9BfgeYfyDVPs7/wqWw/szu5m+ecIfRLF1BSvgUUu/sFKJdEC/wDCa9IjtUlxno2oppPaSUIUSs9BZ5bTLeLLddtH7t79/wACV7Q6ORa5ku1rkuX/AOkz2ZgRJalQYUZqtciLei8y6VN3iRyeXRzNN6BY4cp7UvIOyV54kP1q0K3RoWG19qkz6bJjMkqiKSRrW0pKcz0tm0yHMAuCtwk9Ev8AMhdG7/0mv6gkexs9LSbIyTERG3ql16onrOetnIzU5EgrLw1dci33Jf6iLRI+BPdbK5uc6RsfX+qvwk/3IXT9E1/UDlhZwfcfbVuB+o1jBa62mXIa2UmmO2s9I1oPcS/ARjJlCn5WfsvOy0rER8RzFRGtVFVV5uZETSYcn0hNyFp5KZmobmQ2xEVXKioiJ61VdA8gG763mK/7oLw+op9sY63mLH7obw+oJ9sUh+bdY+yxPwr/AAXfS0dI+0s/En8lduED8cUn5M564ikWGxYwYxovG6YtLoGEV0PTKfT0yJDC4yEKS244tKF7V7SM21l/+JhN/VT4S37kLn+ia9sSvZ+QmpemwocWG5HIi3oqLfpU7uk2xs/Ck4bHzsJFRO97fWvtIqPf8wapfvxejb9Qg1/qp8JXv4I3R9E17Y3j/Bq4RTikqTgndJ5IQk/cGt5JIj/2n8BtllI6vRUYuhe5fYVz5TtRlLSUaRhUeK2O5sVyqkNUcqJmXXrdfcl5GJ7gwYd8oFsc8wenQGo+DNwi/wByd0/Qtf1BtrQ4O/CBpF2USrTsF7rTGhVGLIeUUdszS2h1KlGREvMzyIxlbKx0cn9C/ApvLUKptjMVZd9yKn+lfWdbS3BBwH5K6N50v706AsW0Zcm9+/4Er2h74K0+pUrDOiwqvTpECWSX3XI0hOi61pvuLSSizPI8lFsHWE/DwPhrdEpVx0mVQ65T2J0Ca0bMiO+glNuoPekyPePuAAE1vCLDhulroqbRg8TdaNlbZpUemRva41GozzNet7PTz0tIs88x+FYOYYm1S2VWVTVJo7i3Yek2Zmha3CdWajzzc0nEpcPT0s1pJR7SzDqAAfkk5FkM5EMgAGMiGQAAGD3BMqeKdvUq+Ot+83LXUupi6lrEtlqC0SUZMGvPMnVIbcWScu1Qo8y77mIqqvB+t6rXDMu9+u1hNek1pFWRLTKc0GkJaJni5MaWqNBsaTRqNOlkszzzyAG2sfGKh3pb9VuVUJ+mQqPGiy5K5Cknk09AZm59j/yofIj/AIpPvDyg4h0ar3RZ0So2dUotSuGjv1CmzXmmlNx2zS2t2OpZL00uGnVmZEk07C27AWjgvRLVtWt2idRlToVehR4Ek3SSlRNNU9mFs0S3qQySj/io+8PkLCm5JFYsatzL6JKrPp6oL0VunNrbn6xCW3lqUo9JGmlCdhdqee8AM9cvqJQLrt61JdHqTq7keejxpzTbZxW3m2HHjbcM1ksjNDKzLJJls2mWYTy4RVoux6rKYpNW1dMhSqoyp1DaCnwYkgmJb7BEs1GTSs80rShStmRGR5jYVrCJDlStJ+z6xHtem2nPdqLVNh0xlTT7rjTrS888tEjQ+5uLeZH3ho6hwdaVKnXbXI9Qhs1W6afJpGvYpTEYo8WU4lUhStURKeeNKSIlrM9qS2bVZgTAw61IaQ+ypK23EkpCi2kZGWZGQ9Mi8A8okZqFFZhsJ0WmG0ttp8CUlkRf9CHsAMZAIiLcMgAAMaJF3hkAABgxkYMsyyAEdsY32m5NuQ5EWdDo9qKlIqlakGymIwuMeTxKSThvpIjI8lKaJJ5GZGZZGf2UbGzDGvz+plJu2M/L0XzNnVOoWRsp03EGSklkskdnodsaDJREaTzCveXB4h31XqpW67dLpHOgzYDJRabGYebako0DS68lOk+hCe1SvYRkRnmZEY+6vYD0quyZ0s7jqkF2bVZdW1sQ0tuNOP0k6cZIVlmWig9YR79IvAAPG88e7Ep9vuzWYj1dgvQ6octlLZtLRxSMTzrLrbpJUk1oUWRKItiiPcZGHCddVOoNWoNpxKY46/U48h1lljRImI0dtJqVkfe0lstkRd9wu8RiK6dwSLehUmqUxV0Sy6rceN440FiO2g5UFuGs0NoLIsktEvbmZqMzMzEpVizHp122/dsGaliRRo0yA4S0aWtjyEIM8tuxROMsq27MiUXfIAYsrEGm3rbD10s02oUtmLJmRJMeoNoS+y5GdW06SibWtPbNqMslHsy8gVKTwhrTrFPcqLNKqsZto6U+tMtLSF8QqLptRpqSQtXuSlEeZK0VkRHmkh91j4Nx7cpzkK66nHuxxFak16C9MpjLSoUp91brim9HPbpuKyPeRbAvwODbRqXHrSIs6GiTcc2nLqTkalswmzhxJJyCZQ0yRJ01qUvScVmZ6Z+AiAE0J2kRmDIvAMJzy2j9ADGREeeQyAAAwZEe8ZAAAflZmRbAoRMVLSkWLTsQ5L8qJSakgnGjciuOOpI8+2Q0SzLIkqMz3ERZmYb1kZlkQhF3g4VGZZlOsGq4kyZtGpDyXIUdykRzRoEh1BofSeaXyyd0k6RditCFZHlkAHeZjZhdBfkxnrwiKXEjR5T2pbcdJLb5I1BkpCTIzc006CSM1Lz7EjyPLDGN2FsqPT5Ue8YbjNTcW0wtKHDLNDxsq0z0fciJ0jb0l6JaRZZ5hMLg9t2rYDtBtOrTJdSiFQ3ac68ppo0P0tplplZmaDSZGTOakmWR5mRaOwy0VscFZl6NSK5e06nSrjQqUqpk/TI9QZcQ9UHpiUNm62WrWlT6y00JIjz7XYnICWqtcdIpN7FTolvvTqzIoj055yKlGt4rHcIm2s1GWZrceWSCMyLPTMzILj3CAthq06HdbVu3DI6uxpc1unsRmlS2I0X4S66nWaGTezMkqUpRqSSSUZ5BkqtlzZF6tXlSKqiFJOiyKO8amdYZ6TiXGHElnlm2snNh7FEv+AQ4fB2nRbTpVvLxJqRTKQ3UYTVSZgMNuKgTtE5DBoMjTnpJJSXN6TIthlmRgPd34kUq0bSZvZVNqNVpTxsKNynobUbbLplovKJxaOwLSIzyzVt2EY+CfjJbVNvYrJfh1DW8eZpSp5obKIie8xr2Ypma9ZpqbyMjJBo7IiNRHsHxXXglRKrZU+zbPfj2qdUTGbmzosBt56Q2wREglmrLSURISWkeZ5EPOq4I064L5od/XFPhzp9CSw60pNIjsvPSmkKJDi5BEburJSzWTRHkSstpkWQAc7JuuFe9swrmgsOMNzEqJTLuWmy6hZocbVlszStKknl3yG8yLwBbw7tI7HtCDbbkspb7BuvSZBJ0SdkPOredWRd4jccWZF3iyDKAMGRGWQCIi3DIAAAAAAAAAAAAAAAAAAAAAAAAMZEMgAAAAAAAAAAAAAAAAAAAAAAAAgDGm8FUbESbTpuJlUthEe0FT6QxDfSRyqjxhxJElg0q4yrJKC1eR5ke7bmJ/H5NtJnmZEZ+TcAKfVfhEYwVimV+3o6k0ersWvLnOrTTklJpNQhqik+k0E6ozQrXPGk1pQakpJSSNIkOmY7V53GigYexKpBrlJqDiYMmWzFQ1pqOnLmJlsml5ZqZUaCQRmkkZqURKM0HnPurTmZ5Fmf8AatJHnkX/QAfotwyMEWRZDIAAAAAAAAAAAAAAAAAAAAAAAAAAAxkQyAAAAAAAAAAAD//2Q=="
        val imageName = "gan-vs-vae.jpg"
        val imageForUpload = ImageForUpload(file_name = imageName, contents = imageContent)

        val imageForUploadReceive = PrintifyService.uploadImage(imageForUpload, true)
        LOG.debug("ImageReceived is $imageForUploadReceive")
        assert(imageForUploadReceive != null)
        return imageForUploadReceive!!
    }

    private suspend fun createProductTest(imageForUploadReceive : ImageForUploadReceive) : String {
        val mugProductInfo = MugProductInfo("Test Title", "Test product upload Description", imageForUploadReceive!!.toImage())
        val productId = PrintifyService.createProduct(mugProductInfo)
        LOG.debug("productId after product creation is $productId")
        assert(productId != null)
        return productId!!
    }

    private suspend fun publishProductTest(productId: String) {
        val publishedCode = PrintifyService.publishProduct(productId)
        LOG.debug("publishedCode after product publication is $publishedCode")
        assert(publishedCode == HttpStatusCode.OK)
    }

}