package com.ugurtansal.yemek_tarifleri

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.ugurtansal.yemek_tarifleri.databinding.FragmentTarifBinding
import com.ugurtansal.yemek_tarifleri.model.Tarif
import com.ugurtansal.yemek_tarifleri.roomDb.TarifDAO
import com.ugurtansal.yemek_tarifleri.roomDb.TarifDatabase
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream


class TarifFragment : Fragment() {
    private lateinit var binding: FragmentTarifBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String> //izin istemek için
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent> //galeriye ulaşmak için
    private  var secilenGorselUri: Uri ?=null //seçilen görselin uri'si
    private  var secilenBitmap: Bitmap? =null //seçilen görselin bitmap'i

    private lateinit var db: TarifDatabase;
    private lateinit var tarifDao: TarifDAO;

    private val mDisposable= CompositeDisposable() //RxJava ile veri akışını yönetmek için kullanılır.Hafızayı temizlemek için kullanılır.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        db= Room.databaseBuilder(requireContext(), TarifDatabase::class.java, "Tarifler").build()
        tarifDao=db.tarifDao()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTarifBinding.inflate(inflater, container, false)

        binding.kaydetBtn.setOnClickListener {
            kaydet(it)
        }

        binding.silBtn.setOnClickListener {
            sil(it)
        }

        binding.imageView.setOnClickListener {
            gorselSec(it)
        }


        arguments?.let {
            val bilgi = TarifFragmentArgs.fromBundle(it).bilgi

            if (bilgi.equals("yeni")) {
                // yeni tarif ekle
                binding.silBtn.isEnabled = false
                binding.kaydetBtn.isEnabled = true
                binding.isimTxt.setText("")
                binding.malzemeTxt.setText("")
            } else {
                binding.kaydetBtn.isEnabled = false
                binding.silBtn.isEnabled = true
            }
        }

        return binding.root
    }

    fun kaydet(view: View) {
        // val action = TarifFragmentDirections.actionTarifFragment
        val isim = binding.isimTxt.text.toString()
        val malzeme = binding.malzemeTxt.text.toString()

        if(secilenBitmap !=null){
            val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!, 300)
            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            val tarif = Tarif(isim, malzeme, byteArray)

           // tarifDao.insert(tarif) //=>RXJava olmasa kullanılırdı

            //RxJava
            tarifDao.insert(tarif)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::handleResponseForInsert)  //işlemin sonucu handleResponseForInsert fonksiyonuna gönderilir
        }

    }
    private fun handleResponseForInsert() {
        // bir önceki sayfaya dönülebilir
        val action= TarifFragmentDirections.actionTarifFragmentToListeFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    fun sil(view: View) {
        // val action = TarifFragmentDirections.actionTarifFragment
    }

    fun gorselSec(view: View) {

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) //izin verilmemiş mi
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) // izin istemek için
                {
                    // izin istemek için bir bilgi verilebilir , Snackbar ile gösterilebilir
                    Snackbar.make(view, "Galeriye ulaşıp seçim yapılamlı", Snackbar.LENGTH_INDEFINITE)
                        .setAction(
                            "İzin ver",
                            View.OnClickListener {
                                //izin isteyeceğiz
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }

                        ).show()

                } else {
                    //izin isteyeceğiz
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                // izin verilmiş,galeriye ulaşabiliriz
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        }else{
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) //izin verilmemiş mi
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) // izin istemek için
                {
                    // izin istemek için bir bilgi verilebilir , Snackbar ile gösterilebilir
                    Snackbar.make(view, "Galeriye ulaşıp seçim yapılamlı", Snackbar.LENGTH_INDEFINITE)
                        .setAction(
                            "İzin ver",
                            View.OnClickListener {
                                //izin isteyeceğiz
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }

                        ).show()

                } else {
                    //izin isteyeceğiz
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                // izin verilmiş,galeriye ulaşabiliriz
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        }



    }


    private fun registerLauncher() {
        activityResultLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult() //startActivityForResult=>Galeri sonucunu almak için
        ){
            result->
            if(result.resultCode== AppCompatActivity.RESULT_OK){//Kullanıcı görseli seçti mi
                val intent=result.data
                if(intent!=null){
                    val gorselUri=intent.data
                    if (gorselUri != null) {
                       secilenGorselUri=intent.data

                        try {
                            if (Build.VERSION.SDK_INT>=28){
                                //yeni yöntem
                                val source= ImageDecoder.createSource (
                                    requireActivity().contentResolver,secilenGorselUri!!)
                                secilenBitmap=ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(secilenBitmap)
                            }
                            else{
                                //eski yöntem
                                secilenBitmap= MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    secilenGorselUri
                                )

                                binding.imageView.setImageBitmap(secilenBitmap)
                            }

                        }catch (e:Exception){
                            println(e.localizedMessage)
                        }





                    }
                }
            }
        }

        permissionLauncher=registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){
            result->
            if(result){
                //izin verildi
                //galeriye ulaşabiliriz
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else{
                //izin verilmedi
                Toast   .makeText(requireContext(),"İzin verilmedi",Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    private fun kucukBitmapOlustur(kullaniciBitmap: Bitmap, maxBoyut: Int): Bitmap {
        var width = kullaniciBitmap.width
        var height = kullaniciBitmap.height

        val bitmapOrani : Double= width.toDouble() / height.toDouble()

        if (bitmapOrani > 1) {
            //yatay
            width = maxBoyut
            height = (width / bitmapOrani).toInt()
        } else {
            //dikey
            height = maxBoyut
            width = (height * bitmapOrani).toInt()
        }
        return Bitmap.createScaledBitmap(kullaniciBitmap, width, height, true)
    }

}