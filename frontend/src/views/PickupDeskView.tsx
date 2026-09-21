import React, { useState } from 'react';
import {
  QrCode,
  CheckCircle2,
  Clock,
  MapPin,
  ShieldCheck,
  Printer,
  Download,
  AlertCircle,
  Key,
  RefreshCw,
  FileText,
  UserCheck,
  Building
} from 'lucide-react';
import { api } from '../services/api';
import { PickupAppointment, CollectionReceipt, User } from '../types';

interface PickupDeskViewProps {
  pickups: PickupAppointment[];
  currentUser: User | null;
  onRefresh: () => void;
  onNavigate: (view: string) => void;
}

export const PickupDeskView: React.FC<PickupDeskViewProps> = ({
  pickups,
  currentUser,
  onRefresh,
  onNavigate
}) => {
  const [activeTab, setActiveTab] = useState<'STUDENT_TICKETS' | 'STAFF_SCANNER'>(
    currentUser?.role === 'ROLE_STAFF' || currentUser?.role === 'ROLE_ADMIN' ? 'STAFF_SCANNER' : 'STUDENT_TICKETS'
  );

  // Staff verification input states
  const [inputOtp, setInputOtp] = useState('');
  const [inputQrToken, setInputQrToken] = useState('');
  const [staffNotes, setStaffNotes] = useState('Student ID verified in person.');
  const [isVerifying, setIsVerifying] = useState(false);
  const [verifyError, setVerifyError] = useState<string | null>(null);
  const [generatedReceipt, setGeneratedReceipt] = useState<CollectionReceipt | null>(null);

  // Handle staff verification
  const handleVerifyHandover = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputOtp && !inputQrToken) {
      setVerifyError('Please enter either the 6-digit OTP code or the QR token.');
      return;
    }

    setIsVerifying(true);
    setVerifyError(null);

    try {
      const receipt = await api.verifyPickup({
        otpCode: inputOtp.trim() || undefined,
        qrToken: inputQrToken.trim() || undefined,
        staffNotes
      });
      setGeneratedReceipt(receipt);
      setInputOtp('');
      setInputQrToken('');
      onRefresh();
    } catch (err: any) {
      console.error(err);
      setVerifyError(err.message || 'Verification failed. Please check OTP code.');
    } finally {
      setIsVerifying(false);
    }
  };

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
        <div>
          <div className="inline-flex items-center space-x-1.5 bg-purple-50 text-purple-700 px-3 py-1 rounded-full text-xs font-bold mb-2 border border-purple-200">
            <QrCode className="w-3.5 h-3.5 text-purple-600" />
            <span>Contactless QR & 6-Digit OTP Collection Desk</span>
          </div>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">
            Item Pickup & Collection
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Present your secure QR code or OTP to campus staff to verify ownership and collect your item.
          </p>
        </div>

        {/* View Switcher */}
        <div className="inline-flex p-1 bg-slate-200/80 rounded-2xl self-start md:self-auto">
          <button
            onClick={() => setActiveTab('STUDENT_TICKETS')}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'STUDENT_TICKETS'
                ? 'bg-white text-slate-900 shadow-sm'
                : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            My Pickup Passes ({pickups.length})
          </button>
          <button
            onClick={() => setActiveTab('STAFF_SCANNER')}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center space-x-1.5 ${
              activeTab === 'STAFF_SCANNER'
                ? 'bg-purple-600 text-white shadow-sm'
                : 'text-purple-700 hover:text-purple-900'
            }`}
          >
            <UserCheck className="w-3.5 h-3.5" />
            <span>Staff Handover Desk</span>
          </button>
        </div>
      </div>

      {activeTab === 'STUDENT_TICKETS' ? (
        /* Student QR & OTP Cards */
        pickups.length === 0 ? (
          <div className="bg-white rounded-3xl p-12 border border-slate-200 text-center">
            <div className="w-16 h-16 bg-purple-50 text-purple-600 rounded-3xl flex items-center justify-center mx-auto mb-4">
              <QrCode className="w-8 h-8" />
            </div>
            <h3 className="text-base font-bold text-slate-800">No Scheduled Pickups Yet</h3>
            <p className="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
              Once an item claim is approved by campus staff, your secure pickup QR code and OTP will appear here.
            </p>
            <button
              onClick={() => onNavigate('browse')}
              className="mt-5 px-5 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold shadow transition-colors"
            >
              Browse Found Items to Claim
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {pickups.map((appt) => (
              <div
                key={appt.id}
                className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm hover:shadow-md transition-shadow"
              >
                {/* Header Strip */}
                <div className="p-4 bg-gradient-to-r from-purple-700 to-indigo-800 text-white flex items-center justify-between">
                  <div>
                    <span className="text-[10px] font-bold text-purple-200 uppercase tracking-widest">
                      Official Campus Collection Pass
                    </span>
                    <h3 className="font-bold text-base text-white">{appt.itemTitle}</h3>
                  </div>
                  <span className={`text-[10px] font-black px-2.5 py-1 rounded-full uppercase ${
                    appt.status === 'COMPLETED' ? 'bg-emerald-500 text-white' : 'bg-white/20 text-white'
                  }`}>
                    {appt.status}
                  </span>
                </div>

                <div className="p-6 grid grid-cols-1 sm:grid-cols-2 gap-6 items-center">
                  {/* QR Code Container */}
                  <div className="text-center p-4 bg-slate-50 rounded-2xl border border-slate-200">
                    {appt.qrCodeBase64 ? (
                      <img
                        src={appt.qrCodeBase64}
                        alt="Pickup QR Code"
                        className="w-40 h-40 mx-auto rounded-xl shadow-sm border border-white"
                      />
                    ) : (
                      <div className="w-40 h-40 mx-auto bg-slate-200 rounded-xl flex items-center justify-center text-slate-400">
                        <QrCode className="w-12 h-12" />
                      </div>
                    )}
                    <span className="block text-[10px] text-slate-500 mt-2 font-mono truncate">
                      Token: {appt.qrToken.slice(0, 16)}...
                    </span>
                  </div>

                  {/* Pass Details & 6-Digit OTP */}
                  <div className="space-y-4">
                    <div>
                      <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">
                        One-Time Pickup Code (OTP)
                      </span>
                      <div className="mt-1 inline-block px-4 py-2 bg-purple-50 border border-purple-200 rounded-xl font-mono text-2xl font-black text-purple-900 tracking-widest">
                        {appt.otpCode}
                      </div>
                    </div>

                    <div className="space-y-1.5 text-xs">
                      <div className="flex items-center space-x-1.5 text-slate-700">
                        <Clock className="w-3.5 h-3.5 text-brand-600 flex-shrink-0" />
                        <span className="font-bold">{appt.timeSlot}</span>
                      </div>
                      <div className="flex items-start space-x-1.5 text-slate-600">
                        <MapPin className="w-3.5 h-3.5 text-red-500 flex-shrink-0 mt-0.5" />
                        <span>{appt.pickupLocation}</span>
                      </div>
                      {appt.storageLocation && (
                        <div className="flex items-center space-x-1.5 text-slate-600">
                          <Building className="w-3.5 h-3.5 text-amber-600 flex-shrink-0" />
                          <span>Storage: <strong>{appt.storageLocation}</strong></span>
                        </div>
                      )}
                    </div>

                    {appt.status === 'COMPLETED' && (
                      <div className="p-2.5 bg-emerald-50 rounded-xl border border-emerald-200 text-xs text-emerald-800">
                        <p className="font-bold flex items-center space-x-1">
                          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                          <span>Item Collected!</span>
                        </p>
                        <p className="text-[11px] mt-0.5">Receipt: {appt.receiptNumber}</p>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )
      ) : (
        /* Staff QR & OTP Verification Desk */
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
          {/* Left Form: Scanner & OTP Input */}
          <div className="lg:col-span-6 bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm">
            <div className="flex items-center space-x-3 mb-6">
              <div className="w-10 h-10 rounded-2xl bg-purple-100 text-purple-800 flex items-center justify-center">
                <UserCheck className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-black text-purple-700 uppercase tracking-widest">
                  Staff Verification Desk
                </span>
                <h2 className="text-xl font-bold text-slate-900">Verify Item Handover</h2>
              </div>
            </div>

            {verifyError && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl text-xs text-red-800 flex items-start space-x-2">
                <AlertCircle className="w-4 h-4 text-red-600 flex-shrink-0 mt-0.5" />
                <span>{verifyError}</span>
              </div>
            )}

            <form onSubmit={handleVerifyHandover} className="space-y-4">
              {/* Enter OTP */}
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1.5">
                  Enter Student 6-Digit Pickup OTP *
                </label>
                <div className="relative">
                  <Key className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
                  <input
                    type="text"
                    maxLength={6}
                    value={inputOtp}
                    onChange={(e) => setInputOtp(e.target.value)}
                    placeholder="e.g. 482731"
                    className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-lg font-mono font-bold tracking-widest text-slate-900 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  />
                </div>
                <p className="text-[11px] text-slate-500 mt-1">
                  Demo hint: Alex Chen's sample ID card OTP is <strong>482731</strong>
                </p>
              </div>

              {/* Or Paste QR Token */}
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1.5">
                  Or Scan / Enter QR Token
                </label>
                <div className="relative">
                  <QrCode className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
                  <input
                    type="text"
                    value={inputQrToken}
                    onChange={(e) => setInputQrToken(e.target.value)}
                    placeholder="e.g. cf74891bcae4492987ff632b84291456"
                    className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs font-mono text-slate-900 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  />
                </div>
              </div>

              {/* Staff Notes */}
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1.5">
                  Staff Verification Notes
                </label>
                <input
                  type="text"
                  value={staffNotes}
                  onChange={(e) => setStaffNotes(e.target.value)}
                  className="w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-purple-500"
                />
              </div>

              <button
                type="submit"
                disabled={isVerifying}
                className="w-full py-3.5 rounded-2xl bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white font-bold text-xs uppercase tracking-wider shadow-lg shadow-purple-500/25 transition-all flex items-center justify-center space-x-2"
              >
                {isVerifying ? (
                  <>
                    <RefreshCw className="w-4 h-4 animate-spin" />
                    <span>Verifying Code...</span>
                  </>
                ) : (
                  <>
                    <CheckCircle2 className="w-4 h-4" />
                    <span>Confirm Handover & Generate Receipt</span>
                  </>
                )}
              </button>
            </form>
          </div>

          {/* Right: Digital Collection Receipt Display */}
          <div className="lg:col-span-6">
            {generatedReceipt ? (
              <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-300 shadow-lg relative print:shadow-none">
                <div className="border-b border-slate-200 pb-4 mb-5 flex items-center justify-between">
                  <div>
                    <span className="text-[10px] font-mono font-bold text-purple-700">
                      {generatedReceipt.receiptNumber}
                    </span>
                    <h3 className="font-black text-lg text-slate-900">Digital Collection Receipt</h3>
                  </div>
                  <div className="w-9 h-9 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center">
                    <CheckCircle2 className="w-5 h-5" />
                  </div>
                </div>

                <div className="space-y-3 text-xs">
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span className="text-slate-500">Item Reference</span>
                    <span className="font-bold text-slate-900">{generatedReceipt.itemReferenceId}</span>
                  </div>
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span className="text-slate-500">Item Title</span>
                    <span className="font-bold text-slate-900">{generatedReceipt.itemTitle}</span>
                  </div>
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span className="text-slate-500">Collected By</span>
                    <span className="font-bold text-slate-900">{generatedReceipt.claimantName} ({generatedReceipt.claimantStudentId})</span>
                  </div>
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span className="text-slate-500">Verified By Staff</span>
                    <span className="font-bold text-slate-900">{generatedReceipt.staffName}</span>
                  </div>
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span className="text-slate-500">Collection Point</span>
                    <span className="font-bold text-slate-900">{generatedReceipt.pickupLocation}</span>
                  </div>
                  <div className="flex justify-between py-1 border-b border-slate-100">
                    <span className="text-slate-500">Timestamp</span>
                    <span className="font-bold text-slate-900">{new Date(generatedReceipt.collectedAt).toLocaleString()}</span>
                  </div>
                </div>

                {/* SHA-256 Digital Signature Badge */}
                <div className="mt-5 p-3 rounded-2xl bg-slate-50 border border-slate-200">
                  <p className="text-[10px] font-bold text-slate-500 uppercase tracking-widest">
                    Verifiable SHA-256 Digital Signature
                  </p>
                  <p className="text-[10px] font-mono text-slate-700 break-all mt-1">
                    {generatedReceipt.digitalSignature}
                  </p>
                </div>

                <div className="mt-6 flex gap-3">
                  <button
                    onClick={() => window.print()}
                    className="flex-1 py-2.5 rounded-xl border border-slate-300 text-slate-700 text-xs font-bold hover:bg-slate-50 transition-colors flex items-center justify-center space-x-1.5"
                  >
                    <Printer className="w-3.5 h-3.5" />
                    <span>Print Receipt</span>
                  </button>
                  <button
                    onClick={() => alert(`Digital receipt ${generatedReceipt.receiptNumber} archived in audit logs.`)}
                    className="flex-1 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-700 text-white text-xs font-bold transition-colors flex items-center justify-center space-x-1.5"
                  >
                    <Download className="w-3.5 h-3.5" />
                    <span>Download PDF</span>
                  </button>
                </div>
              </div>
            ) : (
              <div className="bg-slate-50 rounded-3xl p-8 border border-dashed border-slate-300 text-center text-slate-500">
                <FileText className="w-12 h-12 mx-auto text-slate-400 mb-3" />
                <h4 className="font-bold text-slate-700 text-sm">Receipt Output Area</h4>
                <p className="text-xs text-slate-500 mt-1 max-w-xs mx-auto">
                  When a pickup code or QR is verified, an official digital receipt with cryptographic SHA-256 signature will generate here.
                </p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
