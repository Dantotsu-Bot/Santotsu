package ani.dantotsu.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ani.dantotsu.BottomSheetDialogFragment
import ani.dantotsu.databinding.BottomSheetProxyBinding
import ani.dantotsu.settings.saving.PrefManager
import ani.dantotsu.settings.saving.PrefName
import ani.dantotsu.restartApp

class ProxyDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetProxyBinding? = null
    private val binding get() = _binding!!

    private var proxyHost = PrefManager.getVal<String>(PrefName.Socks5ProxyHost).orEmpty()
    private var proxyPort = PrefManager.getVal<String>(PrefName.Socks5ProxyPort).orEmpty()
    private var proxyUsername = PrefManager.getVal<String>(PrefName.Socks5ProxyUsername).orEmpty()
    private var proxyPassword = PrefManager.getVal<String>(PrefName.Socks5ProxyPassword).orEmpty()
    private var authEnabled = PrefManager.getVal<Boolean>(PrefName.ProxyAuthEnabled)
    private val proxyEnabled = PrefManager.getVal<Boolean>(PrefName.EnableSocks5Proxy)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetProxyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            proxyHost.setText(proxyHost)
            proxyPort.setText(proxyPort)
            proxyUsername.setText(proxyUsername)
            proxyPassword.setText(proxyPassword)
            proxyAuthentication.isChecked = authEnabled

            fun updateAuthFields(isChecked: Boolean) {
                proxyUsername.isEnabled = isChecked
                proxyPassword.isEnabled = isChecked
                proxyUsernameLayout.isEnabled = isChecked
                proxyPasswordLayout.isEnabled = isChecked
            }

            updateAuthFields(authEnabled)

            proxySave.setOnClickListener {
                proxyHost = proxyHost.text.toString()
                proxyPort = proxyPort.text.toString()
                proxyUsername = proxyUsername.text.toString()
                proxyPassword = proxyPassword.text.toString()

                PrefManager.setVal(PrefName.Socks5ProxyHost, proxyHost)
                PrefManager.setVal(PrefName.Socks5ProxyPort, proxyPort)
                PrefManager.setVal(PrefName.Socks5ProxyUsername, proxyUsername)
                PrefManager.setVal(PrefName.Socks5ProxyPassword, proxyPassword)

                dismiss()
                if (proxyEnabled) activity?.restartApp()
            }

            proxyAuthentication.setOnCheckedChangeListener { _, isChecked ->
                PrefManager.setVal(PrefName.ProxyAuthEnabled, isChecked)
                updateAuthFields(isChecked)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}