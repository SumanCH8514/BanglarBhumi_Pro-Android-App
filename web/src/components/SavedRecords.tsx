import React from 'react';
import { Bookmark, Trash2, ArrowRight, MapPin, Calendar } from 'lucide-react';
import { SavedRecordItem, LandRecordResult } from '../types';

interface SavedRecordsProps {
  savedList: SavedRecordItem[];
  onOpenRecord: (record: LandRecordResult) => void;
  onDeleteRecord: (id: string) => void;
  lang: 'en' | 'bn';
}

export const SavedRecords: React.FC<SavedRecordsProps> = ({
  savedList,
  onOpenRecord,
  onDeleteRecord,
  lang
}) => {
  return (
    <div className="bg-white rounded-2xl shadow-xl border border-slate-200 p-6 md:p-8 space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-slate-100 gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 flex items-center gap-2">
            <Bookmark className="w-5 h-5 text-emerald-600" />
            <span>{lang === 'bn' ? 'সংরক্ষিত ভূমি রেকর্ড সমূহ' : 'Saved Land & Property Records'}</span>
          </h2>
          <p className="text-sm text-slate-500 mt-1">
            {lang === 'bn'
              ? 'আপনার পূর্বে সংরক্ষিত খতিয়ান ও দাগের রেকর্ডসমূহ অফলাইনে সহজেই দেখুন ও প্রিন্ট করুন'
              : 'Quickly access, review, and print previously bookmarked land records offline'}
          </p>
        </div>

        <span className="self-start sm:self-auto bg-emerald-50 text-emerald-800 font-bold text-xs px-3 py-1.5 rounded-full border border-emerald-200">
          {savedList.length} {lang === 'bn' ? 'টি রেকর্ড সংরক্ষিত' : 'Records saved'}
        </span>
      </div>

      {savedList.length === 0 ? (
        <div className="text-center py-16 bg-slate-50 rounded-2xl border border-dashed border-slate-200 space-y-3">
          <Bookmark className="w-10 h-10 text-slate-300 mx-auto" />
          <h3 className="text-base font-bold text-slate-700">
            {lang === 'bn' ? 'কোনো রেকর্ড সংরক্ষিত নেই' : 'No records bookmarked yet'}
          </h3>
          <p className="text-xs text-slate-400 max-w-sm mx-auto">
            {lang === 'bn'
              ? 'রেকর্ড অনুসন্ধান করার পর উপরের "সংরক্ষণ করুন" বাটনে ক্লিক করে এখানে সেভ করতে পারেন।'
              : 'Search for any Khatian or Plot record and click "Save Record" to store it here for offline reference.'}
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {savedList.map(item => (
            <div
              key={item.id}
              className="p-5 bg-white rounded-xl border border-slate-200 hover:border-emerald-300 shadow-sm hover:shadow transition-all space-y-3 flex flex-col justify-between"
            >
              <div className="space-y-2">
                <div className="flex items-center justify-between gap-2">
                  <span className="bg-emerald-100 text-emerald-800 text-[11px] font-bold px-2 py-0.5 rounded font-mono">
                    {item.metadata.searchType}: {item.metadata.searchNumber}
                  </span>
                  <div className="flex items-center gap-1 text-[11px] text-slate-400">
                    <Calendar className="w-3 h-3" />
                    <span>{new Date(item.savedAt).toLocaleDateString()}</span>
                  </div>
                </div>

                <div className="text-sm font-semibold text-slate-900 flex items-center gap-1.5">
                  <MapPin className="w-4 h-4 text-emerald-600 shrink-0" />
                  <span>{item.metadata.mouzaName}</span>
                </div>

                <div className="text-xs text-slate-500 pl-5.5 space-y-0.5">
                  <div>Block: <span className="font-medium text-slate-700">{item.metadata.blockName}</span></div>
                  <div>District: <span className="font-medium text-slate-700">{item.metadata.districtName}</span></div>
                </div>
              </div>

              <div className="pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                <button
                  onClick={() => onDeleteRecord(item.id)}
                  className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                  title="Remove from saved"
                >
                  <Trash2 className="w-4 h-4" />
                </button>

                <button
                  onClick={() => onOpenRecord({ html: item.html, metadata: item.metadata })}
                  className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-emerald-700 hover:bg-emerald-800 text-white rounded-lg text-xs font-bold transition-all shadow-sm"
                >
                  <span>{lang === 'bn' ? 'বিস্তারিত দেখুন' : 'View Details'}</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
