import React, { useState } from 'react';
import {
  Layers,
  HelpCircle,
  PlusCircle,
  ShieldCheck,
  QrCode,
  CheckCircle2,
  Clock,
  MapPin,
  Calendar,
  ArrowRight
} from 'lucide-react';
import { FoundReport, LostReport, Claim, PickupAppointment } from '../types';

interface MyActivityViewProps {
  myLost: LostReport[];
  myFound: FoundReport[];
  myClaims: Claim[];
  myPickups: PickupAppointment[];
  onNavigate: (view: string) => void;
  onSchedulePickup: (claim: Claim) => void;
}

export const MyActivityView: React.FC<MyActivityViewProps> = ({
  myLost,
  myFound,
  myClaims,
  myPickups,
  onNavigate,
  onSchedulePickup
}) => {
  const [activeTab, setActiveTab] = useState<'CLAIMS' | 'LOST' | 'FOUND'>('CLAIMS');

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">
            My Campus Activity
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Track your lost reports, found items, ownership claims, and pickup appointment schedules.
          </p>
        </div>

        {/* Tab Switcher */}
        <div className="inline-flex p-1 bg-slate-200/80 rounded-2xl">
          <button
            onClick={() => setActiveTab('CLAIMS')}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'CLAIMS' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            My Claims ({myClaims.length})
          </button>
          <button
            onClick={() => setActiveTab('LOST')}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'LOST' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Lost Reports ({myLost.length})
          </button>
          <button
            onClick={() => setActiveTab('FOUND')}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
              activeTab === 'FOUND' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Found Reports ({myFound.length})
          </button>
        </div>
      </div>

      {/* Overview Metric Banner */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-8">
        <div className="p-4 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Active Lost</span>
          <p className="text-2xl font-black text-slate-900 mt-1">{myLost.length}</p>
        </div>
        <div className="p-4 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Reported Found</span>
          <p className="text-2xl font-black text-emerald-700 mt-1">{myFound.length}</p>
        </div>
        <div className="p-4 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Claims Filed</span>
          <p className="text-2xl font-black text-brand-700 mt-1">{myClaims.length}</p>
        </div>
        <div className="p-4 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Pickup Passes</span>
          <p className="text-2xl font-black text-purple-700 mt-1">{myPickups.length}</p>
        </div>
      </div>

      {/* Tab Content */}
      {activeTab === 'CLAIMS' ? (
        myClaims.length === 0 ? (
          <div className="bg-white rounded-3xl p-12 border border-slate-200 text-center">
            <ShieldCheck className="w-12 h-12 text-slate-400 mx-auto mb-3" />
            <h3 className="font-bold text-slate-800 text-base">No Claims Submitted Yet</h3>
            <p className="text-xs text-slate-500 mt-1">Found an item that looks like yours? Claim it in Browse Catalog.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {myClaims.map((c) => (
              <div
                key={c.id}
                className="bg-white rounded-3xl border border-slate-200 p-6 shadow-sm hover:shadow-md transition-shadow flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-3">
                    <span className="text-xs font-mono font-bold text-slate-500">
                      Ref: {c.foundReferenceId}
                    </span>
                    <span className={`px-2.5 py-1 rounded-full text-[10px] font-bold ${
                      c.status === 'APPROVED'
                        ? 'bg-emerald-100 text-emerald-800'
                        : c.status === 'COLLECTED'
                        ? 'bg-blue-100 text-blue-800'
                        : 'bg-amber-100 text-amber-800'
                    }`}>
                      {c.status}
                    </span>
                  </div>

                  <h3 className="font-bold text-slate-900 text-base">{c.foundTitle}</h3>

                  <div className="mt-3 p-3 bg-slate-50 rounded-xl border border-slate-100 text-xs text-slate-600">
                    <p className="font-bold text-slate-700 mb-1">Your Submitted Evidence:</p>
                    <p className="italic">"{c.claimantAnswers}"</p>
                  </div>

                  {c.consistencyScore && (
                    <div className="mt-3 flex items-center space-x-2 text-xs">
                      <span className="font-bold text-slate-500">AI Consistency:</span>
                      <span className={`font-bold px-2 py-0.5 rounded-md text-[11px] ${
                        c.consistencyScore === 'HIGH' ? 'bg-emerald-100 text-emerald-800' : 'bg-yellow-100 text-yellow-800'
                      }`}>
                        {c.consistencyScore}
                      </span>
                    </div>
                  )}

                  {c.adminNotes && (
                    <div className="mt-3 p-2.5 bg-blue-50/60 rounded-xl border border-blue-100 text-xs text-blue-900">
                      <span className="font-bold">Staff Note: </span>
                      {c.adminNotes}
                    </div>
                  )}
                </div>

                <div className="mt-6 pt-4 border-t border-slate-100 flex items-center justify-between">
                  <span className="text-[11px] text-slate-400">
                    Filed: {new Date(c.createdAt).toLocaleDateString()}
                  </span>

                  {c.status === 'APPROVED' && !c.pickupScheduled ? (
                    <button
                      onClick={() => onSchedulePickup(c)}
                      className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold shadow-sm transition-colors flex items-center space-x-1"
                    >
                      <Calendar className="w-3.5 h-3.5" />
                      <span>Schedule Pickup</span>
                    </button>
                  ) : c.pickupScheduled ? (
                    <button
                      onClick={() => onNavigate('pickup')}
                      className="px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold shadow-sm transition-colors flex items-center space-x-1"
                    >
                      <QrCode className="w-3.5 h-3.5" />
                      <span>View Pickup QR</span>
                    </button>
                  ) : null}
                </div>
              </div>
            ))}
          </div>
        )
      ) : activeTab === 'LOST' ? (
        myLost.length === 0 ? (
          <div className="bg-white rounded-3xl p-12 border border-slate-200 text-center">
            <HelpCircle className="w-12 h-12 text-slate-400 mx-auto mb-3" />
            <h3 className="font-bold text-slate-800 text-base">No Lost Reports Logged</h3>
            <button
              onClick={() => onNavigate('report-lost')}
              className="mt-4 px-5 py-2.5 rounded-xl bg-amber-600 text-white text-xs font-bold"
            >
              Report Lost Item
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {myLost.map((item) => (
              <div
                key={item.id}
                className="bg-white rounded-3xl border border-slate-200 p-6 shadow-sm hover:shadow-md transition-shadow"
              >
                <div className="flex items-center justify-between text-xs font-mono font-bold text-slate-500 mb-2">
                  <span>Ref: {item.referenceId}</span>
                  <span className="text-amber-700">{item.status}</span>
                </div>
                <h3 className="font-bold text-slate-900 text-base">{item.title}</h3>
                <p className="text-xs text-slate-600 mt-1 line-clamp-2">{item.description}</p>
                <div className="mt-4 flex items-center justify-between pt-3 border-t border-slate-100">
                  <span className="text-xs text-slate-500 flex items-center space-x-1">
                    <MapPin className="w-3.5 h-3.5 text-amber-600" />
                    <span>{item.locationName}</span>
                  </span>
                  <button
                    onClick={() => onNavigate('matches')}
                    className="px-3 py-1.5 bg-brand-50 hover:bg-brand-100 text-brand-700 text-xs font-bold rounded-xl"
                  >
                    View Matches
                  </button>
                </div>
              </div>
            ))}
          </div>
        )
      ) : (
        /* FOUND */
        myFound.length === 0 ? (
          <div className="bg-white rounded-3xl p-12 border border-slate-200 text-center">
            <PlusCircle className="w-12 h-12 text-slate-400 mx-auto mb-3" />
            <h3 className="font-bold text-slate-800 text-base">No Found Items Reported by You</h3>
            <button
              onClick={() => onNavigate('report-found')}
              className="mt-4 px-5 py-2.5 rounded-xl bg-emerald-600 text-white text-xs font-bold"
            >
              Report Found Item
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {myFound.map((item) => (
              <div
                key={item.id}
                className="bg-white rounded-3xl border border-slate-200 p-6 shadow-sm hover:shadow-md transition-shadow"
              >
                <div className="flex items-center justify-between text-xs font-mono font-bold text-slate-500 mb-2">
                  <span>Ref: {item.referenceId}</span>
                  <span className="text-emerald-700">{item.status}</span>
                </div>
                <h3 className="font-bold text-slate-900 text-base">{item.title}</h3>
                <p className="text-xs text-slate-600 mt-1 line-clamp-2">{item.publicDescription}</p>
                <div className="mt-4 flex items-center justify-between pt-3 border-t border-slate-100 text-xs text-slate-500">
                  <span className="flex items-center space-x-1">
                    <MapPin className="w-3.5 h-3.5 text-emerald-600" />
                    <span>{item.locationName}</span>
                  </span>
                  <span>{new Date(item.foundDate).toLocaleDateString()}</span>
                </div>
              </div>
            ))}
          </div>
        )
      )}
    </div>
  );
};
